package com.excited.friend.service.exam.impl;

import com.excited.friend.domain.exam.dto.ExamQueryDTO;
import com.excited.friend.domain.exam.vo.ExamVO;
import com.excited.friend.manager.ExamCacheManager;
import com.excited.friend.mapper.exam.ExamMapper;
import com.excited.friend.service.exam.IExamService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamServiceImpl implements IExamService {

    @Autowired
    private ExamMapper examMapper;

    private ExamCacheManager examCacheManager;

    @Override
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
        return examMapper.selectExamList(examQueryDTO);
    }

    @Override
    public void redisList(ExamQueryDTO examQueryDTO) {
        // 先查询 Redis 是否有数据
        Long listSize = examCacheManager.getListSize(examQueryDTO.getType());
        List<ExamVO> examVOList;
        if (listSize == null || listSize == 0) {
            // 如果 Redis 中没有数据, 需要从数据库中进行查询, 并且需要将数据库中的数据刷新到 Redis 中
            examVOList = list(examQueryDTO);
            examCacheManager.refreshCache(examQueryDTO.getType());
        }
    }
}
