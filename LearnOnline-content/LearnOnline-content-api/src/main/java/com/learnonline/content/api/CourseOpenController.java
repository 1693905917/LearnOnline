package com.learnonline.content.api;

import com.learnonline.content.model.dto.CoursePreviewDto;
import com.learnonline.content.service.CourseBaseInfoService;
import com.learnonline.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.api
 * @Author: ASUS
 * @CreateTime: 2024-08-04  16:21
 * @Description: 获取课程信息
 * @Version: 1.0
 */
@Api(value = "课程公开查询接口",tags = "课程公开查询接口")
@RestController
@RequestMapping("/open")
public class CourseOpenController {
    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @Autowired
    private CoursePublishService coursePublishService;


    /**
     * 获取课程预览信息
     *
     * @param courseId 课程ID
     * @return 返回课程预览信息的DTO对象
     */
    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getPreviewInfo(@PathVariable("courseId") Long courseId) {
        //获取课程预览信息
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);
        return coursePreviewInfo;
    }

}
