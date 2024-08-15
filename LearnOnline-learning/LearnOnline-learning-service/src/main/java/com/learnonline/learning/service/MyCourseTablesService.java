package com.learnonline.learning.service;

import com.learnonline.base.model.PageResult;
import com.learnonline.learning.model.dto.MyCourseTableParams;
import com.learnonline.learning.model.dto.XcChooseCourseDto;
import com.learnonline.learning.model.dto.XcCourseTablesDto;
import com.learnonline.learning.model.po.XcCourseTables;

/**
 * @description 我的课程表service接口
 * @version 1.0
 */
public interface MyCourseTablesService {

    /**
     * @description 添加选课
     * @param userId 用户id
     * @param courseId 课程id
     * @return com.learnonline.learning.model.dto.XcChooseCourseDto
     */
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId);


    /**
     * @description 判断学习资格
     * @param userId
     * @param courseId
     * @return XcCourseTablesDto 学习资格状态 [{"code":"702001","desc":"正常学习"},{"code":"702002","desc":"没有选课或选课后没有支付"},{"code":"702003","desc":"已过期需要申请续期或重新支付"}]
     */
    public XcCourseTablesDto getLearningStatus(String userId, Long courseId);

    /*
     * @description:保存选课成功状态
     * @author:  HZP
     * @date: 2024/8/15 8:11
     * @param: chooseCourseId
     * @return:
     **/
    boolean saveChooseCourseSuccessStatus(String chooseCourseId);


    /**
     * 查询我的课程表分页信息
     *
     * @param params 查询参数
     * @return 返回我的课程表分页结果
     */
    PageResult<XcCourseTables> myCourseTables(MyCourseTableParams params);
}

