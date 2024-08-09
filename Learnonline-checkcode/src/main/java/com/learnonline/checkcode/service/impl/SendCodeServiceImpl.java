package com.learnonline.checkcode.service.impl;

import com.learnonline.base.execption.LearnOnlineException;
import com.learnonline.checkcode.service.SendCodeService;
import com.learnonline.checkcode.utils.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.checkcode.service.impl
 * @Author: ASUS
 * @CreateTime: 2024-08-09  19:04
 * @Description: 发送验证码服务
 * @Version: 1.0
 */
@Service
@Slf4j
public class SendCodeServiceImpl implements SendCodeService {
    public final Long CODE_TTL = 120L;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public void sendEMail(String email, String code) {
        // 1. 向用户发送验证码
        try {
            MailUtil.sendTestMail(email, code);
        } catch (MessagingException e) {
            log.debug("邮件发送失败：{}", e.getMessage());
            LearnOnlineException.cast("发送验证码失败，请稍后再试");
        }
        // 2. 将验证码缓存到redis，TTL设置为2分钟
        redisTemplate.opsForValue().set(email, code, CODE_TTL, TimeUnit.SECONDS);
    }
}
