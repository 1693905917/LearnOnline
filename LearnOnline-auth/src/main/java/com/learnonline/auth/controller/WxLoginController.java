package com.learnonline.auth.controller;

import com.learnonline.ucenter.model.dto.FindPswDto;
import com.learnonline.ucenter.model.po.XcUser;
import com.learnonline.ucenter.service.VerifyService;
import com.learnonline.ucenter.service.WxAuthService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.auth.controller
 * @Author: ASUS
 * @CreateTime: 2024-08-09  11:19
 * @Description: 微信请求获取授权码
 * @Version: 1.0
 */
@Slf4j
@Controller
public class WxLoginController {

    @Autowired
    WxAuthService wxAuthService;

    @RequestMapping("/wxLogin")
    public String wxLogin(String code, String state) throws IOException {
        log.debug("微信扫码回调,code:{},state:{}",code,state);
        //请求微信申请令牌，拿到令牌查询用户信息，将用户信息写入本项目数据库
        XcUser xcUser = wxAuthService.wxAuth(code);
        if(xcUser==null){
            return "redirect:http://www.learnonline.cn/error.html";
        }
        String username = xcUser.getUsername();
        return "redirect:http://www.learnonline.cn/sign.html?username="+username+"&authType=wx";
    }
}

