package com.learnonline.ucenter.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.ucenter.model.dto
 * @Author: ASUS
 * @CreateTime: 2024-08-10  11:24
 * @Description: 接收注册请求的参数
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    private String cellphone;

    private String checkcode;

    private String checkcodekey;

    private String confirmpwd;

    private String email;

    private String nickname;

    private String password;

    private String username;

}
