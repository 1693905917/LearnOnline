package com.learnonline.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learnonline.content.model.po.CourseTeacher;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 课程-教师关系表 Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface CourseTeacherMapper extends BaseMapper<CourseTeacher> {

}
