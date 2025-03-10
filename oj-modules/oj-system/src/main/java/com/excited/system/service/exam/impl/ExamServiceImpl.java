package com.excited.system.service.exam.impl;

import com.excited.system.domain.exam.dto.ExamQueryDTO;
import com.excited.system.domain.exam.vo.ExamVO;
import com.excited.system.mapper.exam.ExamMapper;
import com.excited.system.service.exam.IExamService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamServiceImpl implements IExamService {

    @Autowired
    private ExamMapper examMapper;

    @Override
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
        return examMapper.selectExamList(examQueryDTO);
    }
}
