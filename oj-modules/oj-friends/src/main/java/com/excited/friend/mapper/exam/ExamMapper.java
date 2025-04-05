package com.excited.friend.mapper.exam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.excited.friend.domain.exam.dto.ExamQueryDTO;
import com.excited.friend.domain.exam.entity.Exam;
import com.excited.friend.domain.exam.vo.ExamVO;

import java.util.List;

public interface ExamMapper extends BaseMapper<Exam> {

    List<ExamVO> selectExamList(ExamQueryDTO examQueryDTO);
}
