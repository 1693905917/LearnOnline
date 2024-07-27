package com.learnonline.content.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learnonline.base.execption.LearnOnlineException;
import com.learnonline.content.mapper.CourseTeacherMapper;
import com.learnonline.content.model.po.CourseTeacher;
import com.learnonline.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.service.impl
 * @Author: ASUS
 * @CreateTime: 2024-07-27  10:36
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    private CourseTeacherMapper courseTeacherMapper;


    /**
     * 获取指定课程的授课教师列表
     *
     * @param courseId 课程的ID
     * @return 授课教师列表
     */
    @Override
    public List<CourseTeacher> getCourseTeacherList(Long courseId) {
        // SELECT * FROM course_teacher WHERE course_id = 117
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(queryWrapper);
        return courseTeachers;
    }

    /**
     * 保存授课教师信息
     *
     * @param courseTeacher 授课教师信息对象
     * @return 保存后的授课教师信息对象
     * @throws LearnOnlineException 新增或修改授课教师失败时抛出
     */
    @Override
    @Transactional
    public CourseTeacher saveCourseTeacher(CourseTeacher courseTeacher) {
        Long id = courseTeacher.getId();
        if (id == null) {
            // id为null，新增教师
            CourseTeacher teacher = new CourseTeacher();
            BeanUtils.copyProperties(courseTeacher, teacher);
            teacher.setCreateDate(LocalDateTime.now());
            int insert = courseTeacherMapper.insert(teacher);
            if(insert<=0){
                LearnOnlineException.cast("新增授课教师失败");
            }
            return getCourseTeacher(teacher);
        }else{
            // id不为null，修改教师
            CourseTeacher teacher = courseTeacherMapper.selectById(id);
            BeanUtils.copyProperties(courseTeacher, teacher);
            int i = courseTeacherMapper.updateById(teacher);
            if(i<=0){
                LearnOnlineException.cast("修改授课教师失败");
            }
            return getCourseTeacher(teacher);
        }
    }

    private CourseTeacher getCourseTeacher(CourseTeacher teacher) {

        return courseTeacherMapper.selectById(teacher.getId());
    }

    /**
     * 删除授课教师
     *
     * @param courseId 课程ID
     * @param teacherId 教师ID
     * @throws LearnOnlineException 如果删除授课教师失败
     */
    @Override
    @Transactional
    public void deleteCourseTeacher(Long courseId, Long teacherId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getId, teacherId);
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        int delete = courseTeacherMapper.delete(queryWrapper);
        if(delete<=0){
            LearnOnlineException.cast("删除授课教师失败");
        }
    }
}
