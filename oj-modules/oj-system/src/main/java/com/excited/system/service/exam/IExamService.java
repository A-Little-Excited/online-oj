package com.excited.system.service.exam;

import com.excited.system.domain.exam.dto.ExamAddDTO;
import com.excited.system.domain.exam.dto.ExamQueryDTO;
import com.excited.system.domain.exam.dto.ExamQuestionAddDTO;
import com.excited.system.domain.exam.vo.ExamVO;

import java.util.List;

public interface IExamService {

    List<ExamVO> list(ExamQueryDTO examQueryDTO);

    String add(ExamAddDTO examAddDTO);

    boolean examQuestionAdd(ExamQuestionAddDTO examQuestionAddDTO);
}
