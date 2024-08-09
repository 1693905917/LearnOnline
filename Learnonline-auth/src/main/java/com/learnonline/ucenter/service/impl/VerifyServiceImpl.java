package com.learnonline.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learnonline.ucenter.mapper.XcUserMapper;
import com.learnonline.ucenter.mapper.XcUserRoleMapper;
import com.learnonline.ucenter.model.dto.PasswordParamDto;
import com.learnonline.ucenter.model.po.XcUser;
import com.learnonline.ucenter.service.VerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.ucenter.service.impl
 * @Author: ASUS
 * @CreateTime: 2024-08-09  19:09
 * @Description: 找回密码
 * @Version: 1.0
 */
@Service
public class VerifyServiceImpl implements VerifyService {

    @Autowired
    XcUserMapper userMapper;

    @Autowired
    XcUserRoleMapper xcUserRoleMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void findPassword(PasswordParamDto findPswDto) {
        String cellphone = findPswDto.getCellphone();
        String email = findPswDto.getEmail();
        if (StringUtils.isEmpty(cellphone) && StringUtils.isEmpty(email)) {
            throw new BadCredentialsException("手机号与邮箱不能为空");
        }

        String password = findPswDto.getPassword();
        String confirmpwd = findPswDto.getConfirmpwd();
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(confirmpwd)) {
            throw new BadCredentialsException("密码不能为空");
        }
        if (!password.equals(confirmpwd)) {
            throw new BadCredentialsException("两次密码不一致");
        }
        String checkcode = findPswDto.getCheckcode();
        if (StringUtils.isEmpty(checkcode)) {
            throw new BadCredentialsException("验证码不能为空");
        }
        String code;
        if (!StringUtils.isEmpty(cellphone)) {
            code = redisTemplate.opsForValue().get(cellphone);
        } else {
            code = redisTemplate.opsForValue().get(email);
        }
        if (StringUtils.isEmpty(code)) {
            throw new BadCredentialsException("验证码已经失效");
        }
        if (!code.equals(checkcode)) {
            throw new BadCredentialsException("验证码不正确");
        }
        XcUser xcUser = null;
        if (StringUtils.isEmpty(cellphone)) {
            xcUser = userMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getCellphone, cellphone));
        } else {
            xcUser = userMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getEmail, email));
        }
        if (xcUser == null) {
            throw new BadCredentialsException("用户不存在");
        }
        xcUser.setPassword(passwordEncoder.encode(password));
        userMapper.updateById(xcUser);

    }


}