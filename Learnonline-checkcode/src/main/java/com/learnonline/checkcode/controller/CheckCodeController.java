package com.learnonline.checkcode.controller;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.learnonline.base.execption.LearnOnlineException;
import com.learnonline.checkcode.model.CheckCodeParamsDto;
import com.learnonline.checkcode.model.CheckCodeResultDto;

import com.learnonline.checkcode.service.CheckCodeService;
import com.learnonline.checkcode.service.SendCodeService;
import com.learnonline.checkcode.utils.MailUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Mr.M
 * @version 1.0
 * @description 验证码服务接口
 * @date 2022/9/29 18:39
 */
@Api(value = "验证码服务接口")
@RestController
public class CheckCodeController {

    @Resource(name = "PicCheckCodeService")
    private CheckCodeService picCheckCodeService;

//    @Autowired
//    SendCodeService sendCodeService;

    @ApiOperation(value="生成验证信息", notes="生成验证信息")
    @PostMapping(value = "/pic")
    public CheckCodeResultDto generatePicCheckCode(CheckCodeParamsDto checkCodeParamsDto){
        return picCheckCodeService.generate(checkCodeParamsDto);
    }

    @ApiOperation(value="校验", notes="校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "业务名称", required = true, dataType = "String", paramType="query"),
            @ApiImplicitParam(name = "key", value = "验证key", required = true, dataType = "String", paramType="query"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, dataType = "String", paramType="query")
    })
    @PostMapping(value = "/verify")
    public Boolean verify(String key, String code){
        Boolean isSuccess = picCheckCodeService.verify(key,code);
        return isSuccess;
    }


    @ApiOperation(value = "发送邮箱验证码", tags = "发送邮箱验证码")
    @PostMapping("/phone")
    public CheckCodeResultDto phoneCode(@RequestParam(value = "cellphone")String cellphone,@RequestParam("email")String email){
        if (StringUtils.isBlank(cellphone) && StringUtils.isBlank(email)){
            throw new LearnOnlineException("手机邮箱不能为空！");
        }
        return picCheckCodeService.phoneCode(cellphone,email);
    }

}
