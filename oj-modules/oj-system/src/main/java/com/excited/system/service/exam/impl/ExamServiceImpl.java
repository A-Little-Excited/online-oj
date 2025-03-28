package com.excited.system.service.exam.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.excited.common.core.enums.ResultCode;
import com.excited.common.security.exception.ServiceException;
import com.excited.system.domain.exam.dto.ExamEditDTO;
import com.excited.system.domain.exam.dto.ExamAddDTO;
import com.excited.system.domain.exam.dto.ExamQueryDTO;
import com.excited.system.domain.exam.dto.ExamQuestionAddDTO;
import com.excited.system.domain.exam.entity.Exam;
import com.excited.system.domain.exam.entity.ExamQuestion;
import com.excited.system.domain.exam.vo.ExamDetailVO;
import com.excited.system.domain.exam.vo.ExamVO;
import com.excited.system.domain.question.entity.Question;
import com.excited.system.domain.question.vo.QuestionVO;
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
        checkExamSaveParams(examAddDTO, null);

        Exam exam = new Exam();
        BeanUtil.copyProperties(examAddDTO, exam);
        examMapper.insert(exam);

        // 由于前端需要接收 examId, 因此此处进行返回
        // 并且需要转换为字符串类型进行返回, 以防 examId 过长导致前端发生数据截断
        return exam.getExamId().toString();
    }

    @Override
    public boolean examQuestionAdd(ExamQuestionAddDTO examQuestionAddDTO) {
        // 先校验竞赛是否存在
        Exam exam = getExam(examQuestionAddDTO.getExamId());
        // 再校验该竞赛是否已开赛
        checkExamStarted(exam);
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

    @Override
    public int examQuestionDelete(Long examId, Long questionId) {
        Exam exam = getExam(examId);
        // 校验该竞赛是否已开赛
        checkExamStarted(exam);

        return examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>()
                .eq(ExamQuestion::getExamId, examId)
                .eq(ExamQuestion::getQuestionId, questionId));
    }

    private void checkExamStarted(Exam exam) {
        if (exam.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.EXAM_STARTED);
        }
    }

    @Override
    public ExamDetailVO detail(Long examId) {
        ExamDetailVO examDetailVO = new ExamDetailVO();

        // 1. 先竞赛的基本信息赋值给返回值
        Exam exam = getExam(examId);
        BeanUtil.copyProperties(exam, examDetailVO);

        // 2. 获取竞赛包含的题目信息, 按照题目顺序返回
        List<ExamQuestion> examQuestionList = examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>()
                .select(ExamQuestion::getQuestionId)
                .eq(ExamQuestion::getExamId, examId)
                .orderByAsc(ExamQuestion::getQuestionOrder));
        if (CollectionUtil.isEmpty(examQuestionList)) {
            // 如果是不包含题目的竞赛, 那么直接进行返回即可
            return examDetailVO;
        }

        // 3. 将 examQuestionList 转化为只包含题目 Id 的集合
        List<Long> questionIdList = examQuestionList.stream().map(ExamQuestion::getQuestionId).toList();

        // 4. 根据题目 Id 集合查询出竞赛包含的所有题目的详细信息
        List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .select(Question::getQuestionId, Question::getTitle, Question::getDifficulty)
                .in(Question::getQuestionId, questionIdList));

        // 5. 将类型为 Question 的 List 转化为 QuestionVO 的 List
        List<QuestionVO> questionVOList = BeanUtil.copyToList(questionList, QuestionVO.class);
        examDetailVO.setExamQuestionList(questionVOList);

        return examDetailVO;
    }

    @Override
    public int edit(ExamEditDTO examEditDTO) {
        // 先判断竞赛是否存在
        Exam exam = getExam(examEditDTO.getExamId());
        // 再校验该竞赛是否已开赛
        checkExamStarted(exam);
        // 进行参数校验
        checkExamSaveParams(examEditDTO, examEditDTO.getExamId());

        exam.setTitle(examEditDTO.getTitle());
        exam.setStartTime(examEditDTO.getStartTime());
        exam.setEndTime(examEditDTO.getEndTime());
        return examMapper.updateById(exam);
    }

    @Override
    public int delete(Long examId) {
        // 先判断竞赛是否存在
        Exam exam = getExam(examId);
        // 再校验该竞赛是否已开赛
        checkExamStarted(exam);
        // 删除竞赛相关的题目, 即使该竞赛不包含题目也不影响
        examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>()
                .eq(ExamQuestion::getExamId, examId));
        return examMapper.deleteById(examId);
    }

    private void checkExamSaveParams(ExamAddDTO examSaveDTO, Long examId) {
        // 不允许添加重复标题的竞赛
        List<Exam> examList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .eq(Exam::getTitle, examSaveDTO.getTitle())
                .ne(examId != null, Exam::getExamId, examId));
        if (CollectionUtil.isNotEmpty(examList)) {
            throw new ServiceException(ResultCode.FAILED_ALREADY_EXISTS);
        }
        // 不允许添加开赛时间早于现在的竞赛
        if (examSaveDTO.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.EXAM_START_TIME_BEFORE_CURRENT_TIME);
        }
        // 不允许添加开赛时间晚于完赛时间的竞赛
        if (examSaveDTO.getStartTime().isAfter(examSaveDTO.getEndTime())) {
            throw new ServiceException(ResultCode.EXAM_START_TIME_AFTER_END_TIME);
        }
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

    private Exam getExam(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }

        return exam;
    }
}
