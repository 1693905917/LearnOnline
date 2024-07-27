package com.learnonline.content.model.dto;

import com.learnonline.content.model.po.Teachplan;
import com.learnonline.content.model.po.TeachplanMedia;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.model.dto
 * @Author: ASUS
 * @CreateTime: 2024-07-26  15:57
 * @Description: 课程计划树型结构dto
 * @Version: 1.0
 */
@Data
@ToString
@ApiModel(value="TeachplanDto", description="课程计划树型结构dto")
public class TeachplanDto extends Teachplan {

    //课程计划关联的媒资信息
    TeachplanMedia teachplanMedia;

    //子结点：小章节list
    List<TeachplanDto> teachPlanTreeNodes;

}

