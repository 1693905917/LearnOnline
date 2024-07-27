package com.learnonline.content.model.dto;

import com.learnonline.content.model.po.CourseCategory;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.model.dto
 * @Author: ASUS
 * @CreateTime: 2024-07-24  17:11
 * @Description: 课程分类树型结点dto
 * @Version: 1.0
 */
//注意：我们CourseCategory是PO类，我们创建CourseCategoryTreeDto是因为CourseCategory中没有关于childrenTreeNodes的属性，
// 并且CourseCategoryTreeDto只是需要在CourseCategory上添加一个属性childrenTreeNodes即可，所以我们选择用extends CourseCategory进行继承。
//添加implements Serializable的作用：我们将来在网络传输的时候，需要进行序列化，则需要实现Serializable接口。
@Data
@ApiModel(value="CourseCategoryTreeDto", description="课程分类树型结点dto")
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    List<CourseCategoryTreeDto> childrenTreeNodes;
}
