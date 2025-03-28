package com.excited.system.service.question.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.excited.common.core.constants.Constants;
import com.excited.common.core.enums.ResultCode;
import com.excited.common.security.exception.ServiceException;
import com.excited.system.domain.question.dto.QuestionAddDTO;
import com.excited.system.domain.question.dto.QuestionEditDTO;
import com.excited.system.domain.question.dto.QuestionQueryDTO;
import com.excited.system.domain.question.entity.Question;
import com.excited.system.domain.question.vo.QuestionDetailVO;
import com.excited.system.domain.question.vo.QuestionVO;
import com.excited.system.mapper.question.QuestionMapper;
import com.excited.system.service.question.IQuestionService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements IQuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public List<QuestionVO> list(QuestionQueryDTO questionQueryDTO) {
        // 获取被排除的 Id 集合, 如果为空直接跳过, 非空再进行处理
        String excludeIdStr = questionQueryDTO.getExcludeIdStr();
        if (StrUtil.isNotEmpty(excludeIdStr)) {
            // 先根据分隔符分割为多个 Id
            String[] excludeIdArr = excludeIdStr.split(Constants.SPLIT_SEM);
            // 转化为元素为 Long 的 Set 集合, 完成去重
            Set<Long> excludeIdSet = Arrays.stream(excludeIdArr)
                    .map(Long::valueOf)
                    .collect(Collectors.toSet());

            questionQueryDTO.setExcludeIdSet(excludeIdSet);
        }

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

    @Override
    public QuestionDetailVO detail(Long questionId) {
        Question question = queryQuestionById(questionId);
        QuestionDetailVO questionDetailVO = new QuestionDetailVO();
        BeanUtil.copyProperties(question, questionDetailVO);
        return questionDetailVO;
    }

    @Override
    public int edit(QuestionEditDTO questionEditDTO) {
        // 先将原本的题目信息查询出来
        Question question = queryQuestionById(questionEditDTO.getQuestionId());
        // 再将新的题目信息逐一进 行赋值
        question.setTitle(questionEditDTO.getTitle());
        question.setContent(questionEditDTO.getContent());
        question.setDifficulty(questionEditDTO.getDifficulty());
        question.setTimeLimit(questionEditDTO.getTimeLimit());
        question.setSpaceLimit(questionEditDTO.getSpaceLimit());
        question.setQuestionCase(questionEditDTO.getQuestionCase());
        question.setDefaultCode(questionEditDTO.getDefaultCode());
        question.setMainFunc(questionEditDTO.getMainFunc());
        // 更新到数据库
        return questionMapper.updateById(question);
    }

    @Override
    public int delete(Long questionId) {
        // 需要查询题目是否存在
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        return questionMapper.deleteById(questionId);
    }

    private Question queryQuestionById(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        return question;
    }
}
