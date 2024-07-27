package com.learnonline.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.model.dto
 * @Author: ASUS
 * @CreateTime: 2024-07-26  11:00
 * @Description: 添加课程dto
 * @Version: 1.0
 */
@Data
@ApiModel(value = "EditCourseDto", description="修改课程基本信息")
public class EditCourseDto extends AddCourseDto {
    //required属性就是用来描述实体中的参数字段是否必传,默认false，如果使用true，则该字段后面会有一个红色的星号
    @ApiModelProperty(value = "课程id", required = true)
    private Long id;
}
