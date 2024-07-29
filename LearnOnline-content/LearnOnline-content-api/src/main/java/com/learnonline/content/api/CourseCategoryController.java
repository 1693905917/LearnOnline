package com.learnonline.content.api;

import com.learnonline.content.model.dto.CourseCategoryTreeDto;
import com.learnonline.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.api
 * @Author: ASUS
 * @CreateTime: 2024-07-24  17:13
 * @Description: 课程分类管理接口
 * @Version: 1.0
 */
@Slf4j
@Api(value = "课程分类管理接口", tags = "课程分类管理接口")
@RestController
public class CourseCategoryController {

    @Autowired
    CourseCategoryService courseCategoryService;
    /**
     * 查询课程分类树节点列表
     *
     * @return 返回课程分类树节点的列表（实际开发中应该返回一个非空的List<CourseCategoryTreeDto>对象）
     */
    @ApiOperation("查询课程分类树节点列表")
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes() {
        return courseCategoryService.queryTreeNodes("1");// 传入1的目的是查询全部，因为要从根节点开始
    }

}
