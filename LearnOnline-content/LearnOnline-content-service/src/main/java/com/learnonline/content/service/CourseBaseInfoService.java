package com.learnonline.content.service;

import com.learnonline.base.model.PageParams;
import com.learnonline.base.model.PageResult;
import com.learnonline.content.model.dto.AddCourseDto;
import com.learnonline.content.model.dto.CourseBaseInfoDto;
import com.learnonline.content.model.dto.EditCourseDto;
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
     * @param companyId 用户所属机构ID
     * @return 课程基本信息列表
     */
    PageResult<CourseBase> queryCourseBaseList(Long companyId,PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);


    /**
     *
     *@description 添加课程基本信息
     * @param companyId  教学机构id
     * @param dto  课程基本信息
     * @return 课理详细信息
     */
    CourseBaseInfoDto createCourseBase(Long companyId,AddCourseDto dto);

    /**
     * 根据课程ID查询课程基本信息。
     * @param courseId 需要查询的课程ID。
     * @return 返回包含课程基本信息的CourseBaseInfoDto对象。
     */
    CourseBaseInfoDto getCourseBaseInfo(Long courseId);
    /**
     * 修改课程信息
     *
     * <p>此方法用于根据提供的公司ID和课程编辑DTO（数据传输对象）来更新课程的基础信息。</p>
     *
     * @param companyId 机构id
     * @param dto       课程信息
     * @return 返回更新后的课程基础信息DTO对象（CourseBaseInfoDto），包含了更新后的课程信息。
     */
    CourseBaseInfoDto  updateCourseBase(Long companyId, EditCourseDto dto);

    /**
     * 删除课程
     *
     * @param companyId 机构ID
     * @param courseId  课程ID
     * @return 无返回值
     * @throws Exception 当删除课程发生异常时抛出
     */
    void deleteCourse(Long companyId, Long courseId);
}
