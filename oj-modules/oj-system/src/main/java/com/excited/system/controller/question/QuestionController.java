package com.excited.system.controller.question;

import com.excited.common.core.controller.BaseController;
import com.excited.common.core.domain.entity.TableDataInfo;
import com.excited.system.domain.question.dto.QuestionQueryDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/question")
@RestController
public class QuestionController extends BaseController {

    @GetMapping("/list")
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
        return null;
    }
}
