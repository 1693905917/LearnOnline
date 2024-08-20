package com.learnonline.content.service;

import com.learnonline.content.model.dto.CoursePreviewDto;
import com.learnonline.content.model.po.CoursePublish;

import java.io.File;

/**
 * @description 课程预览、发布接口
 */
public interface CoursePublishService {


    /**
     * @description 获取课程预览信息
     * @param courseId 课程id
     * @return com.learnonline.content.model.dto.CoursePreviewDto
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);


    /**
     * @description 提交审核
     * @param courseId  课程id
     * @return void
     */
    public void commitAudit(Long companyId,Long courseId);

    /**
     * @description 课程发布接口
     * @param companyId 机构id
     * @param courseId 课程id
     * @return void
     */
    public void publish(Long companyId,Long courseId);

    /**
     * @description 课程静态化
     * @param courseId  课程id
     * @return File 静态化文件
     */
    public File generateCourseHtml(Long courseId);
    /**
     * @description 上传课程静态化页面
     * @param file  静态化文件
     * @return void
     */
    public void  uploadCourseHtml(Long courseId,File file);

    /**
     * 根据课程ID获取课程发布信息
     *
     * @param courseId 课程ID
     * @return 返回课程发布信息，若未找到则返回null
     */
    CoursePublish getCoursePublish(Long courseId);

    /**
     * 查询缓存中的课程发布信息
     * @param courseId
     * @return  课程发布信息
     */
    CoursePublish getCoursePublishCache(Long courseId);
}

