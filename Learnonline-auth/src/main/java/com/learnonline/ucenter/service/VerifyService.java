package com.learnonline.ucenter.service;

import com.learnonline.ucenter.model.dto.FindPswDto;
import com.learnonline.ucenter.model.dto.RegisterDto;

public interface VerifyService {
    void findPassword(FindPswDto findPswDto);

    void register(RegisterDto registerDto);
}
