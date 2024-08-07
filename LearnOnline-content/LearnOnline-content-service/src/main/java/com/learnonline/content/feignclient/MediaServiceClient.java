package com.learnonline.content.feignclient;

import com.learnonline.content.config.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description 媒资管理服务远程接口
 * @version 1.0
 */
// value = "media-api"：指定调用的是哪个服务器，填写服务器名
// configuration = MultipartSupportConfig.class：Feign本身是不具备上传文件功能，所以你需要让Feign添加这个配置拥有上传文件的功能。
@FeignClient(value = "media-api",configuration = MultipartSupportConfig.class,fallbackFactory = MediaServiceClientFallbackFactory.class)
public interface MediaServiceClient {
    @RequestMapping(value = "/media/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadFile(@RequestPart("filedata") MultipartFile upload, @RequestParam(value = "objectName",required=false) String objectName);

}
