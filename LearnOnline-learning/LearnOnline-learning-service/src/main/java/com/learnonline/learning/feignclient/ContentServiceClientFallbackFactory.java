package com.learnonline.learning.feignclient;

import com.learnonline.content.model.po.CoursePublish;
import com.learnonline.content.model.po.Teachplan;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Mr.M
 * @version 1.0
 * @description 获取课程信息远程接口
 * @date 2022/10/25 9:14
 */
@Slf4j
@Component
public class ContentServiceClientFallbackFactory implements FallbackFactory<ContentServiceClient> {
    @Override
    public ContentServiceClient create(Throwable throwable) {
        return new ContentServiceClient() {

            @Override
            public CoursePublish getCoursepublish(Long courseId) {
                log.error("调用内容管理服务查询课程信息发生熔断:{}", throwable.toString(),throwable);
                return null;
            }

            @Override
            public Teachplan getTeachplan(Long teachplanId) {
                log.error("调用内容管理服务查询教学计划发生熔断:{}", throwable.toString(),throwable);
                return null;
            }
        };
    }
}
