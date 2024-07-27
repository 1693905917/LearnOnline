package com.learnonline;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.api
 * @Author: ASUS
 * @CreateTime: 2024-07-23  16:29
 * @Description: 课程内容启动类
 * @Version: 1.0
 */
@EnableSwagger2Doc
@SpringBootApplication
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}

