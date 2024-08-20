package com.learnonline.learning.api;

import com.learnonline.base.execption.LearnOnlineException;
import com.learnonline.base.model.PageResult;
import com.learnonline.learning.service.MyCourseTablesService;
import com.learnonline.learning.util.SecurityUtil;
import com.learnonline.learning.model.dto.MyCourseTableParams;
import com.learnonline.learning.model.dto.XcChooseCourseDto;
import com.learnonline.learning.model.dto.XcCourseTablesDto;
import com.learnonline.learning.model.po.XcCourseTables;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.M
 * @version 1.0
 * @description 我的课程表接口
 * @date 2022/10/25 9:40
 */

@Api(value = "我的课程表接口", tags = "我的课程表接口")
@Slf4j
@RestController
public class MyCourseTablesController {

    @Autowired
    MyCourseTablesService courseTablesService;


    /**
     * 添加选课
     *
     * @param courseId 课程ID
     * @return 返回选课结果
     * @throws LearnOnlineException 如果用户未登录，则抛出异常提示用户登录
     */
    @ApiOperation("添加选课")
    @PostMapping("/choosecourse/{courseId}")
    public XcChooseCourseDto addChooseCourse(@PathVariable("courseId") Long courseId) {
        //登录用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user == null){
            LearnOnlineException.cast("请登录后继续选课");
        }
        String userId = user.getId();
        return  courseTablesService.addChooseCourse(userId, courseId);

    }

    /**
     * 查询学习资格
     *
     * @param courseId 课程ID
     * @return 返回课程学习资格信息
     * @throws LearnOnlineException 如果用户未登录，则抛出异常提示用户登录
     */
    @ApiOperation("查询学习资格")
    @PostMapping("/choosecourse/learnstatus/{courseId}")
    public XcCourseTablesDto getLearnStatus(@PathVariable("courseId") Long courseId) {
        //登录用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user == null){
            LearnOnlineException.cast("请登录后继续选课");
        }
        String userId = user.getId();
        return  courseTablesService.getLearningStatus(userId, courseId);

    }

    /**
     * 我的课程表
     *
     * @param params 查询课程表参数
     * @return 课程表分页结果
     * @throws LearnOnlineException 如果用户未登录，则抛出异常提示用户登录
     */
    @ApiOperation("我的课程表")
    @GetMapping("/mycoursetable")
    public PageResult<XcCourseTables> myCourseTables(MyCourseTableParams params) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if (user == null) {
            LearnOnlineException.cast("请登录后查看课程表");
        }
        String userId = user.getId();
        params.setUserId(userId);
        return courseTablesService.myCourseTables(params);
    }

}
