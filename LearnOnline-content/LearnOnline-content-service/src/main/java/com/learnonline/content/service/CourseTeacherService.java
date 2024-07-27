package com.learnonline.content.service;

import com.learnonline.content.model.po.CourseTeacher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface CourseTeacherService {
    /**
     * 根据课程ID获取课程教师列表
     *
     * @param courseId 课程ID
     * @return 包含课程教师信息的列表
     */
    List<CourseTeacher> getCourseTeacherList( Long courseId);

    /**
     * 保存课程教师信息
     *
     * @param courseTeacher 课程教师对象，包含课程教师信息
     * @return 保存后的课程教师对象
     */
    CourseTeacher saveCourseTeacher(CourseTeacher courseTeacher);

    /**
     * 根据课程ID和教师ID删除课程教师信息
     *
     * @param courseId 课程ID
     * @param teacherId 教师ID
     * @return 无返回值
     */
    void  deleteCourseTeacher( Long courseId, Long teacherId);
}
