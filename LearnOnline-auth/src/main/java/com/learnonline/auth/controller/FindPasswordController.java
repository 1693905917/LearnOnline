package com.learnonline.auth.controller;

import com.learnonline.ucenter.model.dto.FindPswDto;
import com.learnonline.ucenter.model.dto.RegisterDto;
import com.learnonline.ucenter.service.VerifyService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.auth.controller
 * @Author: ASUS
 * @CreateTime: 2024-08-10  09:24
 * @Description: 找回密码
 * @Version: 1.0
 */
@Slf4j
@Controller
public class FindPasswordController {
    @Autowired
    VerifyService verifyService;

    //TODO 1.找回密码有bug,点击找回密码之后再点击账号密码登录会切换《找回密码》界面，但是头部页面显示有用户名。
    //TODO 2.《找回密码》与《注册》都缺少前端对异常处理时，对用户的提示消息。

    @ApiOperation(value = "找回密码", tags = "找回密码")
    @PostMapping("/findpassword")
    public void findPassword(@RequestBody FindPswDto findPswDto) {
        verifyService.findPassword(findPswDto);
    }

    @ApiOperation(value = "注册", tags = "注册")
    @PostMapping("/register")
    public void register(@RequestBody RegisterDto registerDto) {
        verifyService.register(registerDto);
    }
}
