package com.excited.system.service.exam;

import com.excited.system.domain.exam.dto.ExamQueryDTO;
import com.excited.system.domain.exam.vo.ExamVO;

import java.util.List;

public interface IExamService {

    List<ExamVO> list(ExamQueryDTO examQueryDTO);
}
