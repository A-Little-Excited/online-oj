package com.excited.system.controller.exam;

import com.excited.common.core.controller.BaseController;
import com.excited.common.core.domain.entity.TableDataInfo;
import com.excited.system.domain.exam.dto.ExamQueryDTO;
import com.excited.system.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exam")
public class ExamController extends BaseController {

    @Autowired
    private IExamService examService;

    @GetMapping("/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        return getTableDataInfo(examService.list(examQueryDTO));
    }
}
