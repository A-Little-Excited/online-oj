package com.excited.system.service.question.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.excited.common.core.domain.entity.TableDataInfo;
import com.excited.common.core.enums.ResultCode;
import com.excited.common.security.exception.ServiceException;
import com.excited.system.domain.question.dto.QuestionAddDTO;
import com.excited.system.domain.question.dto.QuestionQueryDTO;
import com.excited.system.domain.question.entity.Question;
import com.excited.system.domain.question.vo.QuestionVO;
import com.excited.system.mapper.question.QuestionMapper;
import com.excited.system.service.question.IQuestionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements IQuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public List<QuestionVO> list(QuestionQueryDTO questionQueryDTO) {
        PageHelper.startPage(questionQueryDTO.getPageNum(), questionQueryDTO.getPageSize());
        return questionMapper.selectQuestionList(questionQueryDTO);
    }

    @Override
    public int add(QuestionAddDTO questionAddDTO) {
        // 如果存在标题或内容相同的题目, 则视作重复题目, 不可添加
        List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .eq(Question::getTitle, questionAddDTO.getTitle())
                .or()
                .eq(Question::getContent, questionAddDTO.getContent()));
        if (CollectionUtil.isNotEmpty(questionList)) {
            throw new ServiceException(ResultCode.FAILED_ALREADY_EXISTS);
        }

        Question question = new Question();
        // 将 questionAddDTO 中的属性复制到 question 中
        BeanUtil.copyProperties(questionAddDTO, question);
        return questionMapper.insert(question);
    }
}
