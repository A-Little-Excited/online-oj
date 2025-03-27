package com.excited.system.service.exam.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.excited.common.core.enums.ResultCode;
import com.excited.common.security.exception.ServiceException;
import com.excited.system.domain.exam.dto.ExamAddDTO;
import com.excited.system.domain.exam.dto.ExamQueryDTO;
import com.excited.system.domain.exam.dto.ExamQuestionAddDTO;
import com.excited.system.domain.exam.entity.Exam;
import com.excited.system.domain.exam.entity.ExamQuestion;
import com.excited.system.domain.exam.vo.ExamVO;
import com.excited.system.domain.question.entity.Question;
import com.excited.system.mapper.exam.ExamQuestionMapper;
import com.excited.system.mapper.exam.ExamMapper;
import com.excited.system.mapper.question.QuestionMapper;
import com.excited.system.service.exam.IExamService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ExamServiceImpl extends ServiceImpl<ExamQuestionMapper, ExamQuestion> implements IExamService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Override
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
        return examMapper.selectExamList(examQueryDTO);
    }

    @Override
    public String add(ExamAddDTO examAddDTO) {
        List<Exam> examList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .eq(Exam::getTitle, examAddDTO.getTitle()));
        // 不允许添加重复标题的竞赛
        if (CollectionUtil.isNotEmpty(examList)) {
            throw new ServiceException(ResultCode.FAILED_ALREADY_EXISTS);
        }
        // 不允许添加开赛时间早于现在的竞赛
        if (examAddDTO.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.EXAM_START_TIME_BEFORE_CURRENT_TIME);
        }
        // 不允许添加开赛时间晚于完赛时间的竞赛
        if (examAddDTO.getStartTime().isAfter(examAddDTO.getEndTime())) {
            throw new ServiceException(ResultCode.EXAM_START_TIME_AFTER_END_TIME);
        }

        Exam exam = new Exam();
        BeanUtil.copyProperties(examAddDTO, exam);
        examMapper.insert(exam);

        // 由于前端需要接收 examId, 因此此处进行返回
        // 并且需要转换为字符串类型进行返回, 以防 examId 过长导致前端发生数据截断
        return exam.getExamId().toString();
    }

    @Override
    public boolean examQuestionAdd(ExamQuestionAddDTO examQuestionAddDTO) {
        // 先查询竞赛是否存在
        if (!isExamExist(examQuestionAddDTO.getExamId())) {
            throw new ServiceException(ResultCode.EXAM_NOT_EXISTS);
        }

        // 如果存储 题目Id 的 set 为空, 说明该竞赛不包含题目, 直接返回即可
        Set<Long> questionIdSet = examQuestionAddDTO.getQuestionIdSet();
        if (CollectionUtil.isEmpty(questionIdSet)) {
            return true;
        }

        // 批量查询 question 是否存在
        List<Question> questionList = questionMapper.selectBatchIds(questionIdSet);
        // 如果查询出来的 questionList 为空或者两个集合大小不一致, 说明存在 questionId 对应不到的 question
        if (CollectionUtil.isEmpty(questionList) || questionList.size() < questionIdSet.size()) {
            throw new ServiceException(ResultCode.EXAM_QUESTION_NOT_EXISTS);
        }

        // 进行批量插入
        return saveBatchExamQuestion(examQuestionAddDTO, questionIdSet);
    }

    private boolean saveBatchExamQuestion(ExamQuestionAddDTO examQuestionAddDTO, Set<Long> questionIdSet) {
        // 将 examQuestionAddDTO 中包含的信息转化为一个个 examQuestion, 然后添加到下述 List 中
        List<ExamQuestion> examQuestionList = new ArrayList<>();
        int order = 1;
        for (Long questionId : questionIdSet) {
            ExamQuestion examQuestion = new ExamQuestion();
            examQuestion.setExamId(examQuestionAddDTO.getExamId());
            examQuestion.setQuestionId(questionId);
            examQuestion.setQuestionOrder(order++);
            examQuestionList.add(examQuestion);
        }
        // 批量插入
        return saveBatch(examQuestionList);
    }

    private boolean isExamExist(Long examId) {
        Exam exam = examMapper.selectById(examId);
        return exam != null;
    }
}
