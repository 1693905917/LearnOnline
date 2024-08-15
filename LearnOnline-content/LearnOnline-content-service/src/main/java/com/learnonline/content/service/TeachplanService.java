package com.learnonline.content.service;

import com.learnonline.content.model.dto.BindTeachplanMediaDto;
import com.learnonline.content.model.dto.SaveTeachplanDto;
import com.learnonline.content.model.dto.TeachplanDto;
import com.learnonline.content.model.po.Teachplan;
import com.learnonline.content.model.po.TeachplanMedia;

import java.util.List;

/**
 * @description 课程基本信息管理业务接口
 * @version 1.0
 */

public interface TeachplanService {
    /**
     * @description 查询课程计划树型结构
     * @param courseId  课程id
     * @return List<TeachplanDto>
     */
    public List<TeachplanDto> findTeachplanTree(long courseId);

    /**
     * @description 保存课程计划
     * @param teachplanDto  课程计划信息
     * @return void
     */
    public void saveTeachplan(SaveTeachplanDto teachplanDto);


    /**
     * 删除教学计划
     *
     * @param teachplanId 教学计划ID
     * @return 无返回值
     */
    public void deleteTeachplan(Long teachplanId);


    /**
     * 根据传入的排序方式和课程计划ID对课程计划进行排序
     *
     * @param moveType 排序方式，例如：升序、降序等
     * @param teachplanId 课程计划ID
     * @return 无返回值
     */
    public void orderByTeachplan(String moveType, Long teachplanId);

    /**
     * @description 教学计划绑定媒资
     * @param bindTeachplanMediaDto
     * @return com.learnonline.content.model.po.TeachplanMedia
     */
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    /** 解绑教学计划与媒资信息
     * @param teachPlanId       教学计划id
     * @param mediaId           媒资信息id
     */
    void unAssociationMedia(Long teachPlanId, String mediaId);

    Teachplan getTeachplan(Long teachplanId);
}
