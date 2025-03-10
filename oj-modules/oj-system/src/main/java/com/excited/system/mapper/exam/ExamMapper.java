package com.excited.system.mapper.exam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.excited.system.domain.exam.dto.ExamQueryDTO;
import com.excited.system.domain.exam.vo.ExamVO;

import java.util.List;

public interface ExamMapper extends BaseMapper<ExamVO> {
    List<ExamVO> selectExamList(ExamQueryDTO examQueryDTO);
}
