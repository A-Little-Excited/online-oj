package com.excited.job.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.excited.common.core.constants.CacheConstants;
import com.excited.common.core.constants.Constants;
import com.excited.common.redis.service.RedisService;
import com.excited.job.domain.entity.Exam;
import com.excited.job.mapper.exam.ExamMapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExamXxlJob {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private RedisService redisService;

    @XxlJob("examListOrganizeHandler")
    public void examListOrganizeHandler() {
        // 重新整理竞赛列表, 就可以直接分别从数据库中查询出"未完赛"和"历史竞赛"数据
        // 然后再写入到缓存中即可, 整体逻辑和 friend 服务中 ExamCacheManager 类的"刷新缓存"的方法内容类似
        System.out.println("**********  examListOrganizeHandler  **********");

        // 查询"未完赛"的竞赛列表并刷新到缓存中
        List<Exam> timeExamList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
                .gt(Exam::getEndTime, LocalDateTime.now())    // 未完赛即是结束时间大于当前时间
                .eq(Exam::getStatus, Constants.TRUE)
                .orderByDesc(Exam::getCreateTime));
        refreshCache(timeExamList, CacheConstants.EXAM_TIME_LIST_KEY);
        //查询"历史"的竞赛列表并刷新到缓存中
        List<Exam> historyExamList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
                .le(Exam::getEndTime, LocalDateTime.now())    // 历史竞赛即是结束时间小于等于当前时间
                .eq(Exam::getStatus, Constants.TRUE)
                .orderByDesc(Exam::getCreateTime));
        refreshCache(historyExamList, CacheConstants.EXAM_HISTORY_LIST_KEY);
    }

    public void refreshCache(List<Exam> examList, String examListType) {
        // 如果竞赛列表中没有任何竞赛, 就直接进行返回
        if (CollectionUtil.isEmpty(examList)) {
            return;
        }

        Map<String, Exam> examMap = new HashMap<>();
        List<Long> examIdList = new ArrayList<>();
        for (Exam exam : examList) {
            // 存储竞赛基本信息需要 key 和 value, 因此此处将多个竞赛信息组装为多个键值对
            examMap.put(getExamDetailKey(exam.getExamId()), exam);
            // 存储竞赛列表时, 需要 key 和 存储了多个 examId 的 List
            // 因此此处将多个 examId 存储到 List 中
            examIdList.add(exam.getExamId());
        }

        // 批量存储竞赛基本信息, 一次性存储多个竞赛信息键值对
        // 即便原先 Redis 存在相同的数据, 也会被直接覆盖
        redisService.multiSet(examMap);

        // 存储竞赛列表中的 examIdList
        // 由于 rightPushAll 是尾插到列表中, 如果原先列表中存在相同的数据, 再次进行插入时不会被覆盖
        // 因此采用先删除旧数据, 再直接存储新数据的方式
        redisService.deleteObject(examListType);
        redisService.rightPushAll(examListType, examIdList);
    }

    private String getExamDetailKey(Long examId) {
        return CacheConstants.EXAM_DETAIL_KEY_PREFIX + examId;
    }
}
