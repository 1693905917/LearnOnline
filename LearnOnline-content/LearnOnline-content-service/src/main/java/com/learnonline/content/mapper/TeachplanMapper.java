package com.learnonline.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learnonline.content.model.dto.TeachplanDto;
import com.learnonline.content.model.po.Teachplan;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface TeachplanMapper extends BaseMapper<Teachplan> {
    /**
     * @description 查询某课程的课程计划，组成树型结构
     * @param courseId 课程id
     */
    public List<TeachplanDto> selectTreeNodes(long courseId);
}
