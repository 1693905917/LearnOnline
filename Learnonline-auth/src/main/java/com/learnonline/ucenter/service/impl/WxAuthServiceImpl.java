package com.learnonline.ucenter.service.impl;

import com.learnonline.ucenter.model.dto.AuthParamsDto;
import com.learnonline.ucenter.model.dto.XcUserExt;
import com.learnonline.ucenter.service.AuthService;
import org.springframework.stereotype.Service;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.ucenter.service.impl
 * @Author: ASUS
 * @CreateTime: 2024-08-07  20:01
 * @Description: 微信扫码登录
 * @Version: 1.0
 */
@Service("wx_authservice")
public class WxAuthServiceImpl implements AuthService {
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        return null;
    }
}
