package com.learnonline.content.service;

import com.learnonline.content.model.dto.CourseCategoryTreeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface CourseCategoryService {
    /**
     * 查询课程分类树形结构节点列表
     *
     * @param id 节点ID，为根节点ID时查询整棵树，为子节点ID时查询该节点及其子节点
     * @return 课程分类树形结构节点列表
     */
   public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
