package com.excited.friend.test;

import com.excited.common.core.controller.BaseController;
import com.excited.common.core.domain.entity.R;
import com.excited.common.message.service.AliSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController extends BaseController {

    @Autowired
    private AliSmsService aliSmsService;

    @GetMapping("/sendCode")
    public R<Void> TestSendCode(String phone, String code) {
        return toR(aliSmsService.sendCode(phone, code));
    }
}
