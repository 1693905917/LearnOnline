package com.learnonline.content.api;

import com.learnonline.base.execption.ValidationGroups;
import com.learnonline.base.model.PageParams;
import com.learnonline.base.model.PageResult;
import com.learnonline.content.model.dto.AddCourseDto;
import com.learnonline.content.model.dto.CourseBaseInfoDto;
import com.learnonline.content.model.dto.EditCourseDto;
import com.learnonline.content.model.dto.QueryCourseParamsDto;
import com.learnonline.content.model.po.CourseBase;
import com.learnonline.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.api
 * @Author: ASUS
 * @CreateTime: 2024-07-23  16:28
 * @Description: 课程信息编辑接口
 * @Version: 1.0
 */
@Api(value = "课程信息编辑接口", tags = "课程信息编辑接口")
@RestController
public class CourseBaseInfoController {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    /**
     * 课程查询接口
     *
     * @param pageParams        分页参数
     * @param queryCourseParams 查询课程参数（可选）
     * @return 课程列表分页结果
     */
    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParams) {
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParams);
        return courseBasePageResult;
    }

    /**
     * 新增课程基础信息
     *
     * @param dto 课程基础信息的添加请求体，包括课程名称、课程描述等
     * @return CourseBaseInfoDto 课程基础信息的响应体，这里返回null仅作为示例，实际业务中应该返回创建后的课程基础信息
     */
    @ApiOperation("新增课程基础信息")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(ValidationGroups.Insert.class) AddCourseDto dto) {
        //机构id，由于认证系统没有上线暂时硬编码
        //TODO: 机构id从认证系统获取
        Long companyId = 1232141425L;
//        int i = 1/0;
        return courseBaseInfoService.createCourseBase(companyId, dto);
    }

    /**
     * 根据课程ID查询课程基础信息。
     *
     * <p>通过GET请求，并指定课程ID作为路径变量，查询并返回该课程的基础信息。</p>
     *
     * @param courseId 课程的唯一标识符ID。
     * @return 返回包含课程基础信息的CourseBaseInfoDto对象。
     */
    @ApiOperation("根据课程id查询课程基础信息")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable("courseId") Long courseId) {
        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }
    @ApiOperation("修改课程基础信息")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated({ValidationGroups.Update.class}) EditCourseDto editCourseDto) {
        //TODO 机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 1232141425L;
        return courseBaseInfoService.updateCourseBase(companyId, editCourseDto);
    }

}
