package com.learnonline.content.service.impl;

import com.learnonline.content.service.CourseCategoryService;
import com.learnonline.content.service.CourseTeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CourseTeacherServiceImplTest {
    @Autowired
    CourseTeacherService courseTeacherService;
    @Test
    void getCourseTeacherList() {
        courseTeacherService.getCourseTeacherList(1L);
    }

    @Test
    void saveCourseTeacher() {
        courseTeacherService.saveCourseTeacher(null);
    }

    @Test
    void deleteCourseTeacher() {
        courseTeacherService.deleteCourseTeacher(1L, 2L);
    }
}