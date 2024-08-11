package com.learnonline.ucenter.service;

import com.learnonline.ucenter.model.dto.AuthParamsDto;
import com.learnonline.ucenter.model.dto.XcUserExt;

public interface AuthService {
    /**
     * @description 认证方法
     * @param authParamsDto 认证参数
     * @return com.learnonline.ucenter.model.po.XcUser 用户信息
     */
    XcUserExt execute(AuthParamsDto authParamsDto);

}
