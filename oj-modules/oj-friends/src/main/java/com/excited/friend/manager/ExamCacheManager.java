package com.excited.friend.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.excited.common.core.constants.CacheConstants;
import com.excited.common.core.constants.Constants;
import com.excited.common.core.enums.ExamListType;
import com.excited.common.redis.service.RedisService;
import com.excited.friend.domain.exam.dto.ExamQueryDTO;
import com.excited.friend.domain.exam.entity.Exam;
import com.excited.friend.domain.exam.vo.ExamVO;
import com.excited.friend.mapper.exam.ExamMapper;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 竞赛缓存存储管理类
 */
@Component
public class ExamCacheManager {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private RedisService redisService;

    public Long getListSize(Integer examListType) {
        String examListKey = getExamListKey(examListType);
        return redisService.getListSize(examListKey);
    }

    public List<ExamVO> getExamVOList(ExamQueryDTO examQueryDTO) {
        // 先对分页进行处理: 计算本次查询的数据量的始末下标
        int start = (examQueryDTO.getPageNum() - 1) * examQueryDTO.getPageSize();
        int end = start + examQueryDTO.getPageSize() - 1;  // 注意下标是从 0 开始的, 因此需要 -1

        String examListKey = getExamListKey(examQueryDTO.getType());
        // 先获取到竞赛列表包含的所有竞赛的 examId
        List<Long> examIdList = redisService.getCacheListByRange(examListKey, start, end, Long.class);
        // 再查询出 examId 对应的竞赛基本信息
        List<ExamVO> examVOList = assembleExamVOList(examIdList);
        if (CollectionUtil.isEmpty(examVOList)) {
            // 如果 assembleExamVOList 返回 null, 说明 Redis 中的数据存在问题
            // 此时需要从数据库中查询数据, 并且需要重新刷新缓存
            examVOList = getExamListByDB(examQueryDTO);
            refreshCache(examQueryDTO.getType());
        }
        return examVOList;
    }

    /**
     * 刷新缓存, 将数据库数据同步到缓存中
     * @param examListType 竞赛列表类型
     */
    public void refreshCache(Integer examListType) {
        List<Exam> examList = new ArrayList<>();
        if (ExamListType.EXAM_TIME_LIST.getValue().equals(examListType)) {
            // 查询"未完赛"的竞赛列表
            examList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                    .select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
                    .gt(Exam::getEndTime, LocalDateTime.now())    // 未完赛即是结束时间大于当前时间
                    .eq(Exam::getStatus, Constants.TRUE)
                    .orderByDesc(Exam::getCreateTime));
        } else if (ExamListType.EXAM_HISTORY_LIST.getValue().equals(examListType)) {
            //查询"历史"的竞赛列表
            examList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                    .select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
                    .le(Exam::getEndTime, LocalDateTime.now())    // 历史竞赛即是结束时间小于等于当前时间
                    .eq(Exam::getStatus, Constants.TRUE)
                    .orderByDesc(Exam::getCreateTime));
        }
        // 如果数据库中没有数据, 则直接返回
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
        redisService.deleteObject(getExamListKey(examListType));
        redisService.rightPushAll(getExamListKey(examListType), examIdList);
    }

    /**
     * 从数据库中查询数据
     * @param examQueryDTO 竞赛查询实体
     * @return 竞赛列表
     */
    private List<ExamVO> getExamListByDB(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
        return examMapper.selectExamList(examQueryDTO);
    }

    /**
     * 根据多个 examId 从 Redis 中批量获取竞赛基本信息
     * @param examIdList 多个 examId 集合
     * @return 多个竞赛基本信息
     */
    private List<ExamVO> assembleExamVOList(List<Long> examIdList) {
        if (CollectionUtil.isEmpty(examIdList)) {
            // 如果 Redis 中该类型竞赛列表包含的竞赛 Id 为空, 那么说明 Redis 中的数据有问题
            // 说明 Redis 中的数据存在问题, 直接返回 null 交给调用方处理
            return null;
        }
        // 将所有竞赛对应的"竞赛基本信息" key 组装起来
        // 竞赛基本信息 key ==> e:d:examId
        List<String> examDetailKeyList = new ArrayList<>();
        for (Long examId : examIdList) {
            examDetailKeyList.add(getExamDetailKey(examId));
        }
        // 使用组装之后的所有 key 进行批量查询
        List<ExamVO> examVOList = redisService.multiGet(examDetailKeyList, ExamVO.class);
        // 查询过程中可能存在某个竞赛对应的 key 查询不到竞赛基本信息, 导致 examVOList 存在 null 值
        // 调用以下方法排除 examVOList 中的值为 null 的元素
        CollUtil.removeNull(examVOList);

        // 经过排除 null 值之后的 examVOList 如果为空或者数量和 examId 的数量对应不上
        // 说明 Redis 中的数据存在问题, 直接返回 null 交给调用方处理
        if (CollectionUtil.isEmpty(examVOList) || examVOList.size() != examIdList.size()) {
            return null;
        }
        return examVOList;
    }

    private String getExamListKey(Integer examListType) {
        if (ExamListType.EXAM_TIME_LIST.getValue().equals(examListType)) {
            return CacheConstants.EXAM_TIME_LIST_KEY;
        } else if (ExamListType.EXAM_HISTORY_LIST.getValue().equals(examListType)) {
            return CacheConstants.EXAM_HISTORY_LIST_KEY;
        }
        return "";
    }

    private String getExamDetailKey(Long examId) {
        return CacheConstants.EXAM_DETAIL_KEY_PREFIX + examId;
    }
}
