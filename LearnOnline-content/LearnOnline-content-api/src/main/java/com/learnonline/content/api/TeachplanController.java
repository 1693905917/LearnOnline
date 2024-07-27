package com.learnonline.content.api;

import com.learnonline.content.model.dto.SaveTeachplanDto;
import com.learnonline.content.model.dto.TeachplanDto;
import com.learnonline.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.api
 * @Author: ASUS
 * @CreateTime: 2024-07-26  15:59
 * @Description: 课程计划编辑接口
 * @Version: 1.0
 */
@Api(value = "课程计划编辑接口",tags = "课程计划编辑接口")
@RestController
public class TeachplanController {
    @Autowired
    TeachplanService teachplanService;

    /**
     * 查询课程计划树形结构
     *
     * @param courseId 课程Id，必填项
     * @return 返回课程计划树形结构列表
     */
    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId",name = "课程Id",required = true,dataType = "Long",paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
        return teachplanService.findTeachplanTree(courseId);
    }
    /**
     * 课程计划创建或修改
     *
     * @param teachplan 包含课程计划信息的SaveTeachplanDto对象
     * @return 无返回值
     */
    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public  void  saveTeachplan(@RequestBody @Validated SaveTeachplanDto teachplan){
        teachplanService.saveTeachplan(teachplan);
    }

    /**
     * 删除课程计划
     *
     * @param teachplanId 课程计划ID
     * @return 无返回值
     */
    @ApiOperation("课程计划删除")
    @DeleteMapping("/teachplan/{teachplanId}")
   public void deleteTeachplan(@PathVariable Long teachplanId){
        teachplanService.deleteTeachplan(teachplanId);
    }

    /**
     * 课程计划排序
     *
     * @param moveType 排序类型，如升序、降序等
     * @param teachplanId 课程计划ID
     * @return 无返回值
     */
    @ApiOperation("课程计划排序")
    @PostMapping("/teachplan/{moveType}/{teachplanId}")
    public void orderByTeachplan(@PathVariable String moveType,@PathVariable Long teachplanId){
        teachplanService.orderByTeachplan(moveType, teachplanId);
    }
}
