package com.learnonline.content.service;

import com.learnonline.base.model.PageParams;
import com.learnonline.base.model.PageResult;
import com.learnonline.content.model.dto.QueryCourseParamsDto;
import com.learnonline.content.model.po.CourseBase;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.service
 * @Author: ASUS
 * @CreateTime: 2024-07-23  20:43
 * @Description: 课程基本信息管理业务接口
 * @Version: 1.0
 */
public interface CourseBaseInfoService {
    /**
     * @description 课程查询接口
     * @param pageParams 分页参数
     * @param queryCourseParamsDto 查询条件
     * @return 课程基本信息列表
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);
}
