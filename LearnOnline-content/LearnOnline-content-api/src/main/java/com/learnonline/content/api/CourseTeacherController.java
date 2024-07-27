package com.learnonline.content.api;

import com.learnonline.content.model.po.CourseTeacher;
import com.learnonline.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.api
 * @Author: ASUS
 * @CreateTime: 2024-07-27  10:24
 * @Description: 教师信息相关接口
 * @Version: 1.0
 */
@Slf4j
@RestController
@Api(value = "教师信息相关接口", tags = {"教师信息相关接口"})
public class CourseTeacherController {

    @Autowired
    private CourseTeacherService courseTeacherService;
    /**
     * 查询教师信息接口
     *
     * @param courseId 课程ID
     * @return 包含课程教师信息的列表
     */
    @ApiOperation("查询教师信息接口")
    @GetMapping("/courseTeacher/list/{courseId}")
    List<CourseTeacher> getCourseTeacherList(@PathVariable("courseId") Long courseId){
        return courseTeacherService.getCourseTeacherList(courseId);
    }

    /**
     * 添加/修改教师信息接口
     *
     * @param courseTeacher 包含教师信息的CourseTeacher对象
     * @return 返回保存后的CourseTeacher对象
     */
    @ApiOperation("添加/修改教师信息接口")
    @PostMapping("/courseTeacher")
    public CourseTeacher saveCourseTeacher(@RequestBody CourseTeacher courseTeacher) {

        return courseTeacherService.saveCourseTeacher(courseTeacher);
    }

    /**
     * 删除教师信息接口
     *
     * @param courseId 课程ID
     * @param teacherId 教师ID
     * @return 无返回值
     */
    @ApiOperation("删除教师信息接口")
    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    void  deleteCourseTeacher(@PathVariable("courseId") Long courseId, @PathVariable("teacherId") Long teacherId) {
        courseTeacherService.deleteCourseTeacher(courseId, teacherId);
    }



}
