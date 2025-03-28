package com.excited.system.controller.exam;

import com.excited.common.core.controller.BaseController;
import com.excited.common.core.domain.entity.R;
import com.excited.common.core.domain.entity.TableDataInfo;
import com.excited.system.domain.exam.dto.ExamEditDTO;
import com.excited.system.domain.exam.dto.ExamAddDTO;
import com.excited.system.domain.exam.dto.ExamQueryDTO;
import com.excited.system.domain.exam.dto.ExamQuestionAddDTO;
import com.excited.system.domain.exam.vo.ExamDetailVO;
import com.excited.system.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exam")
public class ExamController extends BaseController {

    @Autowired
    private IExamService examService;

    @GetMapping("/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        return getTableDataInfo(examService.list(examQueryDTO));
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody ExamAddDTO examAddDTO) {
        return R.ok(examService.add(examAddDTO));
    }

    @PostMapping("/examQuestion/add")
    public R<Void> examQuestionAdd(@RequestBody ExamQuestionAddDTO examQuestionAddDTO) {
        return toR(examService.examQuestionAdd(examQuestionAddDTO));
    }

    @DeleteMapping("/examQuestion/delete")
    public R<Void> examQuestionDelete(Long examId, Long questionId) {
        return toR(examService.examQuestionDelete(examId, questionId));
    }

    @GetMapping("/detail")
    public R<ExamDetailVO> detail(Long examId) {
        return R.ok(examService.detail(examId));
    }

    @PutMapping("/edit")
    public R<Void> edit(@RequestBody ExamEditDTO examEditDTO) {
        return toR(examService.edit(examEditDTO));
    }
}
