package com.learnonline.ucenter.service;

import com.learnonline.ucenter.model.po.XcUser;
/*
 * @description:微信服务接口
 * @author:  HZP
 * @date: 2024/8/9 12:15
 * @param:
 * @return:
 **/
public interface WxAuthService {
    /**
     * 通过微信授权码获取微信用户信息
     *
     * @param code 微信授权码
     * @return XcUser 返回包含微信用户信息的对象
     */
    public XcUser wxAuth(String code);
}
