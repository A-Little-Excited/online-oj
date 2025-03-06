package com.excited.system.service.question;

import com.excited.common.core.domain.entity.TableDataInfo;
import com.excited.system.domain.question.dto.QuestionAddDTO;
import com.excited.system.domain.question.dto.QuestionQueryDTO;
import com.excited.system.domain.question.vo.QuestionVO;

import java.util.List;

public interface IQuestionService {

    List<QuestionVO> list(QuestionQueryDTO questionQueryDTO);

    int add(QuestionAddDTO questionAddDTO);
}
