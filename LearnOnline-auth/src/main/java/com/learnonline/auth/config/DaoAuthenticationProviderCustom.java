package com.learnonline.auth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.auth.config
 * @Author: ASUS
 * @CreateTime: 2024-08-07  19:46
 * @Description: 自定义DaoAuthenticationProvider 重写了DaoAuthenticationProvider的校验的码的方法，因为我们统一认证入口，有一些认证方式不需要校验密
 * @Version: 1.0
 */
@Component
public class DaoAuthenticationProviderCustom extends DaoAuthenticationProvider {

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    }
}
