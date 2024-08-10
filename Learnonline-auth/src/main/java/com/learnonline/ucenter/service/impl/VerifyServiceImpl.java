package com.learnonline.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learnonline.base.execption.LearnOnlineException;
import com.learnonline.ucenter.mapper.XcUserMapper;
import com.learnonline.ucenter.mapper.XcUserRoleMapper;
import com.learnonline.ucenter.model.dto.FindPswDto;
import com.learnonline.ucenter.model.dto.RegisterDto;
import com.learnonline.ucenter.model.po.XcUser;
import com.learnonline.ucenter.model.po.XcUserRole;
import com.learnonline.ucenter.service.VerifyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

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
    StringRedisTemplate redisTemplate;
    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    XcUserRoleMapper xcUserRoleMapper;
    /**
     * 重写父类方法，用于找回用户密码
     *
     * @param findPswDto 找回密码DTO对象，包含邮箱、验证码、密码及确认密码
     * @throws RuntimeException 当验证码输入错误、两次输入的密码不一致或用户不存在时抛出异常
     */
    @Override
    public void findPassword(FindPswDto findPswDto) {
        //用户输入的邮箱
        String email = findPswDto.getEmail();
        //用户输入的验证码
        String checkCode = findPswDto.getCheckcode();
        Boolean verify = verify(email, checkCode);
        if (!verify) {
           LearnOnlineException.cast("验证码输入错误");
        }
        String password = findPswDto.getPassword();
        String confirmpwd = findPswDto.getConfirmpwd();
        if (!password.equals(confirmpwd)) {
            LearnOnlineException.cast("两次输入的密码不一致");
        }
        LambdaQueryWrapper<XcUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(XcUser::getEmail, findPswDto.getEmail());
        XcUser user = xcUserMapper.selectOne(lambdaQueryWrapper);
        if (user == null) {
            LearnOnlineException.cast("用户不存在");
        }
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        xcUserMapper.updateById(user);
    }

    @Override
    @Transactional
    public void register(RegisterDto registerDto) {
        String uuid = UUID.randomUUID().toString();
        String email = registerDto.getEmail();
        String checkcode = registerDto.getCheckcode();
        Boolean verify = verify(email, checkcode);
        if (!verify) {
            throw new RuntimeException("验证码输入错误");
        }
        String password = registerDto.getPassword();
        String confirmpwd = registerDto.getConfirmpwd();
        if (!password.equals(confirmpwd)) {
            throw new RuntimeException("两次输入的密码不一致");
        }
        LambdaQueryWrapper<XcUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(XcUser::getEmail, registerDto.getEmail());
        XcUser user = xcUserMapper.selectOne(lambdaQueryWrapper);
        if (user != null) {
            throw new RuntimeException("用户已存在，一个邮箱只能注册一个账号");
        }
        XcUser xcUser = new XcUser();
        BeanUtils.copyProperties(registerDto, xcUser);
        xcUser.setPassword(new BCryptPasswordEncoder().encode(password));
        xcUser.setId(uuid);
        xcUser.setUtype("101001");  // 学生类型
        xcUser.setStatus("1");
        xcUser.setName(registerDto.getNickname());
        xcUser.setCreateTime(LocalDateTime.now());
        int insert = xcUserMapper.insert(xcUser);
        if (insert <= 0) {
            throw new RuntimeException("新增用户信息失败");
        }
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(uuid);
        xcUserRole.setUserId(uuid);
        xcUserRole.setRoleId("17");
        xcUserRole.setCreateTime(LocalDateTime.now());
        int insert1 = xcUserRoleMapper.insert(xcUserRole);
        if (insert1 <= 0) {
            throw new RuntimeException("新增用户角色信息失败");
        }
    }

    /**
     * 验证用户输入的验证码是否正确
     *
     * @param email 用户邮箱
     * @param checkCode 用户输入的验证码
     * @return 如果验证码正确返回true，否则返回false
     */
    private Boolean verify(String email, String checkCode) {
//        String key = "checkCode:" + email;
        String code = redisTemplate.opsForValue().get(email);
        if (code == null) {
            return false;
        }
        redisTemplate.delete(email);
        return checkCode.equals(code);
    }
}