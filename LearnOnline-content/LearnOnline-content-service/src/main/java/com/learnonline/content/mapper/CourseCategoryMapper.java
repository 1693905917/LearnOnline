package com.learnonline.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learnonline.content.model.dto.CourseCategoryTreeDto;
import com.learnonline.content.model.po.CourseCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {
    //由于mybatis自带的方法里面没有关于sql向下递归的方法，所以需要自己写一个
   public List<CourseCategoryTreeDto> selectTreeNodes(String id);
}
