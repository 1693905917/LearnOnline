package com.learnonline.content.model.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.model.dto
 * @Author: ASUS
 * @CreateTime: 2024-08-04  15:51
 * @Description: 课程预览数据模型
 * @Version: 1.0
 */
@Data
@ToString
public class CoursePreviewDto {

    //课程基本信息,课程营销信息
    CourseBaseInfoDto courseBase;

    //课程计划信息
    List<TeachplanDto> teachplans;
    //师资信息暂时不加...


}
