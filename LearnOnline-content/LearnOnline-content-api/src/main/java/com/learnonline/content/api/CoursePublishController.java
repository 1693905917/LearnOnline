package com.learnonline.content.api;

import com.alibaba.fastjson.JSON;
import com.learnonline.content.model.dto.CourseBaseInfoDto;
import com.learnonline.content.model.dto.CoursePreviewDto;
import com.learnonline.content.model.dto.TeachplanDto;
import com.learnonline.content.model.po.CoursePublish;
import com.learnonline.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.api
 * @Author: ASUS
 * @CreateTime: 2024-08-04  15:36
 * @Description: 课程预览，发布
 * @Version: 1.0
 */
@Api(value = "课程预览发布接口",tags = "课程预览发布接口")
@Controller
public class CoursePublishController {
    @Autowired
    CoursePublishService coursePublishService;

    /**
     * 课程预览
     *
     * @param courseId 课程ID
     * @return 返回ModelAndView对象，包含课程预览信息和页面视图名称
     */
    @GetMapping("/coursepreview/{courseId}")
    @ApiOperation("课程预览")
    public ModelAndView preview(@PathVariable("courseId") Long courseId){
        //获取课程预览信息
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("model",coursePreviewInfo);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }

    /**
     * 课程审核
     *
     * @param courseId 课程ID
     * @return 无返回值，通过修改数据库状态实现审核功能
     */
    @ResponseBody
    @ApiOperation("课程审核")
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId){
        Long companyId = 1232141425L;
        coursePublishService.commitAudit(companyId,courseId);
    }

    /**
     * 发布课程
     *
     * @param courseId 课程ID
     * @return 无返回值，执行发布课程操作
     */
    @ApiOperation("课程发布")
    @ResponseBody
    @PostMapping ("/coursepublish/{courseId}")
    public void coursepublish(@PathVariable("courseId") Long courseId){
        Long companyId = 1232141425L;
        coursePublishService.publish(companyId,courseId);
    }

    /**
     * 查询课程发布信息
     *
     * @param courseId 课程ID
     * @return 返回课程发布信息
     */
    @ApiOperation("查询课程发布信息")
    @ResponseBody
    @GetMapping("/r/coursepublish/{courseId}")
    public CoursePublish getCoursepublish(@PathVariable("courseId") Long courseId) {
        CoursePublish coursePublish = coursePublishService.getCoursePublish(courseId);
        return coursePublish;
    }


    @ApiOperation("获取课程发布信息")
    @ResponseBody
    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getCoursePublish(@PathVariable("courseId") Long courseId) {
        //封装数据
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();

        //查询课程发布表
        CoursePublish coursePublish = coursePublishService.getCoursePublish(courseId);
        if(coursePublish == null){
            return coursePreviewDto;
        }
        //开始向coursePreviewDto填充数据
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(coursePublish,courseBaseInfoDto);
        //课程计划信息
        String teachplanJson = coursePublish.getTeachplan();
        //转成List<TeachplanDto>
        List<TeachplanDto> teachplanDtos = JSON.parseArray(teachplanJson, TeachplanDto.class);
        coursePreviewDto.setCourseBase(courseBaseInfoDto);
        coursePreviewDto.setTeachplans(teachplanDtos);
        return coursePreviewDto;

    }


}
