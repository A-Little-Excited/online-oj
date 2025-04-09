package com.excited.system.manager;

import com.excited.common.core.constants.CacheConstants;
import com.excited.common.redis.service.RedisService;
import com.excited.system.domain.exam.entity.Exam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 竞赛缓存存储管理类
 */
@Component
public class ExamCacheManager {

    @Autowired
    private RedisService redisService;

    public void addCache(Exam exam) {
        // 存储到"未完赛"竞赛列表中
        redisService.leftPushForList(getExamTimeListKey(), exam.getExamId());
        // 存储竞赛基本信息
        redisService.setCacheObject(getExamDetailKey(exam.getExamId()), exam);
    }

    public void deleteCache(Long examId) {
        // 删除"未完赛"竞赛列表中的信息
        redisService.removeForList(getExamTimeListKey(), examId);
        // 删除竞赛基本信息
        redisService.deleteObject(getExamDetailKey(examId));
    }

    private String getExamTimeListKey() {
        return CacheConstants.EXAM_TIME_LIST_KEY;
    }

    private String getExamHistoryListKey() {
        return CacheConstants.EXAM_HISTORY_LIST_KEY;
    }

    private String getExamDetailKey(Long examId) {
        return CacheConstants.EXAM_DETAIL_KEY_PREFIX + examId;
    }
}
