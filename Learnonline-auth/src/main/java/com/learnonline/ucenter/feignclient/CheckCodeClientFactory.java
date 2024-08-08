package com.learnonline.ucenter.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.ucenter.feignclient
 * @Author: ASUS
 * @CreateTime: 2024-08-08  15:29
 * @Description: 验证码降级逻辑
 * @Version: 1.0
 */
@Slf4j
@Component
public class CheckCodeClientFactory implements FallbackFactory<CheckCodeClient> {
    @Override
    public CheckCodeClient create(Throwable throwable) {
        return new CheckCodeClient() {
            @Override
            public Boolean verify(String key, String code) {
                log.debug("调用验证码服务熔断异常:{}", throwable.getMessage());
                return null;
            }
        };
    }
}

