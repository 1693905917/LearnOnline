package com.learnonline.content.service.impl;

import com.learnonline.base.model.PageParams;
import com.learnonline.base.model.PageResult;
import com.learnonline.content.model.dto.CourseCategoryTreeDto;
import com.learnonline.content.model.dto.QueryCourseParamsDto;
import com.learnonline.content.model.po.CourseBase;
import com.learnonline.content.service.CourseBaseInfoService;
import com.learnonline.content.service.CourseCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CourseCategoryServiceImplTest {
    @Autowired
    CourseCategoryService courseCategoryService;

    /**
     * 测试课程分类服务
     *
     * @return 无返回值，因为这是一个测试方法，主要用于验证 {@link CourseCategoryService#queryTreeNodes(String)} 方法
     *         是否能正确返回课程分类树形结构的DTO列表
     */
    @Test
    void testCourseCategoryService() {
        List<CourseCategoryTreeDto> categoryTreeDtos = courseCategoryService.queryTreeNodes("1");
        System.out.println(categoryTreeDtos);
    }

}