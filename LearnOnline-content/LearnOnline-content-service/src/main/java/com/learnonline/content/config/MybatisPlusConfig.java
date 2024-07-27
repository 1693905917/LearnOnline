package com.learnonline.content.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.config
 * @Author: ASUS
 * @CreateTime: 2024-07-23  17:06
 * @Description: 定义分页拦截器
 * @Version: 1.0
 */
@Configuration
@MapperScan("com.learnonline.content.mapper")
public class MybatisPlusConfig {
    /**
     * 定义分页拦截器
     */
    @Bean  //想让@Bean生效就要声明@Configuration注解
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}
