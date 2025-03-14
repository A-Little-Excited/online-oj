package com.excited.system.controller.exam;

import com.excited.common.core.controller.BaseController;
import com.excited.common.core.domain.entity.R;
import com.excited.common.core.domain.entity.TableDataInfo;
import com.excited.system.domain.exam.dto.ExamAddDTO;
import com.excited.system.domain.exam.dto.ExamQueryDTO;
import com.excited.system.domain.exam.dto.ExamQuestionAddDTO;
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
    public R<Void> add(@RequestBody ExamAddDTO examAddDTO) {
        return toR(examService.add(examAddDTO));
    }

    @PostMapping("/examQuestion/add")
    public R<Void> examQuestionAdd(@RequestBody ExamQuestionAddDTO examQuestionAddDTO) {
        return toR(examService.examQuestionAdd(examQuestionAddDTO));
    }
}
