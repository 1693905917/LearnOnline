package com.learnonline.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.feignclient
 * @Author: ASUS
 * @CreateTime: 2024-08-06  21:01
 * @Description: 搜索服务远程接口降级处理
 * @Version: 1.0
 */
@Slf4j
@Component
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable throwable) {

        return new SearchServiceClient() {
            @Override
            public Boolean add(CourseIndex courseIndex) {
                throwable.printStackTrace();
                log.debug("调用搜索发生熔断走降级方法,熔断异常:", throwable.getMessage());
                //触发降级处理就返回false
                return false;
            }
        };
    }
}

