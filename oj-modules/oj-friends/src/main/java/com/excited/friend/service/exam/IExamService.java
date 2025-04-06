package com.excited.friend.service.exam;

import com.excited.common.core.domain.entity.TableDataInfo;
import com.excited.friend.domain.exam.dto.ExamQueryDTO;
import com.excited.friend.domain.exam.vo.ExamVO;

import java.util.List;

public interface IExamService {

    List<ExamVO> list(ExamQueryDTO examQueryDTO);

    TableDataInfo redisList(ExamQueryDTO examQueryDTO);
}
