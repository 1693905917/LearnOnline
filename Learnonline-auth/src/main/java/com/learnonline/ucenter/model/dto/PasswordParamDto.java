package com.learnonline.ucenter.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.checkcode.model
 * @Author: ASUS
 * @CreateTime: 2024-08-09  18:53
 * @Description: 接收找回密码的参数信息
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordParamDto {

    String cellphone;

    String email;

    String checkcodekey;

    String checkcode;

    String password;

    String confirmpwd;
}