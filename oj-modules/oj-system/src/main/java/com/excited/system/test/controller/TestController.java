package com.excited.system.test.controller;

import com.excited.common.redis.service.RedisService;
import com.excited.system.domain.sysUser.dto.ValidationDTO;
import com.excited.system.domain.sysUser.entity.SysUser;
import com.excited.system.test.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ITestService testService;

    @Autowired
    private RedisService redisService;

    @GetMapping("/list")
    public List<?> list() {
        return testService.list();
    }

    @GetMapping("/add")
    public String add() {
        return testService.add();
    }

    @GetMapping("/redisTest")
    public String redisTest() {
        SysUser sysUser = new SysUser();
        sysUser.setUserAccount("redisTest");
        redisService.setCacheObject("u", sysUser);

        return redisService.getCacheObject("u", SysUser.class).toString();
    }

    @GetMapping("/validation")
    public String validation(@Validated ValidationDTO validationDTO) {
        return "参数校验测试";
    }
}
