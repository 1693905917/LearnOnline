package com.learnonline.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.feignclient
 * @Author: ASUS
 * @CreateTime: 2024-08-06  16:46
 * @Description: Feign定义降级逻辑
 * @Version: 1.0
 */
@Slf4j
@Component
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
    @Override
    public MediaServiceClient create(Throwable throwable) {
        return new MediaServiceClient(){
            @Override
            public String uploadFile(MultipartFile upload, String objectName) {
                //降级方法
                log.debug("调用媒资管理服务上传文件时发生熔断，异常信息:{}",throwable.toString(),throwable);
                return null;//至于返回什么由程序员自己来定
            }
        };

    }
}
