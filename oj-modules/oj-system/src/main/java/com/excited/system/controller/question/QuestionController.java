package com.excited.system.controller.question;

import com.excited.common.core.controller.BaseController;
import com.excited.common.core.domain.entity.R;
import com.excited.common.core.domain.entity.TableDataInfo;
import com.excited.system.domain.question.dto.QuestionAddDTO;
import com.excited.system.domain.question.dto.QuestionEditDTO;
import com.excited.system.domain.question.dto.QuestionQueryDTO;
import com.excited.system.domain.question.vo.QuestionDetailVO;
import com.excited.system.service.question.IQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/question")
@RestController
@Tag(name = "题目管理接口")
public class QuestionController extends BaseController {

    @Autowired
    private IQuestionService questionService;

    @Operation(summary = "获取题目列表", description = "获取题目列表信息")
    @ApiResponse(responseCode = "1000", description = "操作成功")
    @ApiResponse(responseCode = "2000", description = "服务繁忙, 请稍后重试")
    @Parameters(value = {
            @Parameter(name = "questionQueryDTO", in = ParameterIn.QUERY, description = "题目列表查询信息")
    })
    @GetMapping("/list")
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
        return getTableDataInfo(questionService.list(questionQueryDTO));
    }

    @Operation(summary = "添加题目信息", description = "根据所提供的信息添加题目")
    @ApiResponse(responseCode = "1000", description = "操作成功")
    @ApiResponse(responseCode = "2000", description = "服务繁忙, 请稍后重试")
    @Parameters(value = {
            @Parameter(name = "questionAddDTO", description = "添加题目信息, 请求体参数")
    })
    @PostMapping("/add")
    public R<Void> add(@Validated @RequestBody QuestionAddDTO questionAddDTO) {
        return toR(questionService.add(questionAddDTO));
    }

    @Operation(summary = "查询题目详情", description = "根据所提供的 questionId 查询题目详情")
    @ApiResponse(responseCode = "1000", description = "操作成功")
    @ApiResponse(responseCode = "2000", description = "服务繁忙, 请稍后重试")
    @ApiResponse(responseCode = "3003", description = "资源不存在")
    @Parameters(value = {
            @Parameter(name = "questionId", in = ParameterIn.QUERY, description = "题目Id")
    })
    @GetMapping("/detail")
    public R<QuestionDetailVO> detail(Long questionId) {
        return R.ok(questionService.detail(questionId));
    }

    @Operation(summary = "查询题目详情", description = "根据所提供的 questionId 查询题目详情")
    @ApiResponse(responseCode = "1000", description = "操作成功")
    @ApiResponse(responseCode = "2000", description = "服务繁忙, 请稍后重试")
    @ApiResponse(responseCode = "3003", description = "资源不存在")
    @Parameters(value = {
            @Parameter(name = "questionEditDTO", description = "编辑题目信息, 请求体参数")
    })
    @PutMapping("/edit")
    public R<Void> edit(@RequestBody QuestionEditDTO questionEditDTO) {
        return toR(questionService.edit(questionEditDTO));
    }

    @DeleteMapping("/delete")
    public R<Void> delete(Long questionId) {
        return toR(questionService.delete(questionId));
    }
}
