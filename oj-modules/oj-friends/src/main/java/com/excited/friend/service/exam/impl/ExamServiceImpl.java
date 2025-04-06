package com.excited.friend.service.exam.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.excited.common.core.domain.entity.TableDataInfo;
import com.excited.friend.domain.exam.dto.ExamQueryDTO;
import com.excited.friend.domain.exam.vo.ExamVO;
import com.excited.friend.manager.ExamCacheManager;
import com.excited.friend.mapper.exam.ExamMapper;
import com.excited.friend.service.exam.IExamService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamServiceImpl implements IExamService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ExamCacheManager examCacheManager;

    @Override
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
        return examMapper.selectExamList(examQueryDTO);
    }

    @Override
    public TableDataInfo redisList(ExamQueryDTO examQueryDTO) {
        // 先查询 Redis 是否有数据
        Long totalData = examCacheManager.getListSize(examQueryDTO.getType());
        List<ExamVO> examVOList;
        if (totalData == null || totalData <= 0) {
            // 如果 Redis 中没有数据, 需要从数据库中进行查询, 并且需要将数据库中的数据刷新到 Redis 中
            examVOList = list(examQueryDTO);
            examCacheManager.refreshCache(examQueryDTO.getType());

            // 从数据库获取数据, 可以直接使用 PageHelper 来获取总数
            totalData = new PageInfo<>(examVOList).getTotal();
        } else {
            examVOList = examCacheManager.getExamVOList(examQueryDTO);

            // 由于从 Redis 中获取数据可能会出错导致需要重新刷新缓存, 导致前后数据量不同, 因此需要重新获取数据总量
            totalData = examCacheManager.getListSize(examQueryDTO.getType());
        }

        // 封装响应结果
        if (CollectionUtil.isEmpty(examVOList)) {
            return TableDataInfo.empty();
        }
        return TableDataInfo.success(examVOList, totalData);
    }
}
