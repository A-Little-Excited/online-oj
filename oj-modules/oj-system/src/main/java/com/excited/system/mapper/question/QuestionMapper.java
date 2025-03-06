package com.excited.system.mapper.question;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.excited.system.domain.question.dto.QuestionQueryDTO;
import com.excited.system.domain.question.entity.Question;
import com.excited.system.domain.question.vo.QuestionVO;

import java.util.List;

public interface QuestionMapper extends BaseMapper<Question> {

    List<QuestionVO> selectQuestionList(QuestionQueryDTO questionQueryDTO);
}
