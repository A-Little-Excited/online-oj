package com.excited.system.controller.user;

import com.excited.common.core.controller.BaseController;
import com.excited.common.core.domain.entity.R;
import com.excited.common.core.domain.entity.TableDataInfo;
import com.excited.system.domain.user.dto.UserQueryDTO;
import com.excited.system.domain.user.dto.UserUpdateStatusDTO;
import com.excited.system.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    @GetMapping("/list")
    public TableDataInfo list(UserQueryDTO userQueryDTO) {
        return getTableDataInfo(userService.list(userQueryDTO));
    }

    @PutMapping("/updateStatus")
    public R<Void> updateStatus(@RequestBody UserUpdateStatusDTO userUpdateStatusDTO) {
        return toR(userService.updateStatus(userUpdateStatusDTO));
    }
}
