package com.learnonline.content.api;

import com.learnonline.base.model.PageParams;
import com.learnonline.base.model.PageResult;
import com.learnonline.content.model.dto.QueryCourseParamsDto;
import com.learnonline.content.model.po.CourseBase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.api
 * @Author: ASUS
 * @CreateTime: 2024-07-23  16:28
 * @Description: 课程信息编辑接口
 * @Version: 1.0
 */
@Api(value = "课程信息编辑接口",tags = "课程信息编辑接口")
@RestController
public class CourseBaseInfoController {
    /**
     * 课程查询接口
     *
     * @param pageParams 分页参数
     * @param queryCourseParams 查询课程参数（可选）
     * @return 课程列表分页结果
     */
    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required=false) QueryCourseParamsDto queryCourseParams){
//        CourseBase courseBase = new CourseBase();
//        courseBase.setName("测试名称");
//        courseBase.setCreateDate(LocalDateTime.now());
//        List<CourseBase> courseBases = new ArrayList();
//        courseBases.add(courseBase);
//        PageResult pageResult = new PageResult<CourseBase>(courseBases,10,1,10);
//        return pageResult;

        return null;
    }


}
