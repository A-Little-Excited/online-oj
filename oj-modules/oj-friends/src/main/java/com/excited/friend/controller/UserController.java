package com.excited.friend.controller;

import com.excited.common.core.constants.HttpConstants;
import com.excited.common.core.controller.BaseController;
import com.excited.common.core.domain.entity.R;
import com.excited.friend.domain.dto.UserGetCodeDTO;
import com.excited.friend.domain.dto.UserLoginDTO;
import com.excited.friend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    // 由于需要传递手机号, 因此虽然是获取信息, 但还是使用 Post 请求, 将参数放在请求体中进行传递
    // 并且虽然需要获取验证码, 但是是发送到用户而不是返回给前端, 因此使用 R<Void>
    @PostMapping("/sendCode")
    public R<Void> sendCode(@RequestBody UserGetCodeDTO userGetCodeDTO) {
        return toR(userService.sendCode(userGetCodeDTO));
    }

    @PostMapping("/code/login")
    public R<String> codeLogin(@RequestBody UserLoginDTO userLoginDTO) {
        return R.ok(userService.codeLogin(userLoginDTO));
    }

    @DeleteMapping("/logout")
    public R<Void> logout(@RequestHeader(HttpConstants.AUTHORIZATION) String token) {
        return toR(userService.logout(token));
    }
}
