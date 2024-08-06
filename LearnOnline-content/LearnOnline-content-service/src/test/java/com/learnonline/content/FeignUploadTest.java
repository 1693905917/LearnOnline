package com.learnonline.content;

import com.learnonline.content.config.MultipartSupportConfig;
import com.learnonline.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content
 * @Author: ASUS
 * @CreateTime: 2024-08-06  16:22
 * @Description: 测试使用feign远程上传文件
 * @Version: 1.0
 */
@SpringBootTest
public class FeignUploadTest {
    @Autowired
    MediaServiceClient mediaServiceClient;

    //远程调用，上传文件
    @Test
    public void test() {
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(new File("E:\\Java\\IDEA_Practice\\xueCheng-plus\\develop\\upload\\1.html"));
        String upload = mediaServiceClient.uploadFile(multipartFile, "course/1.html");
        System.out.println(upload);
    }

}
