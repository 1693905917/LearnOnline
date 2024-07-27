package com.learnonline.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learnonline.base.execption.LearnOnlineException;
import com.learnonline.content.mapper.TeachplanMapper;
import com.learnonline.content.mapper.TeachplanMediaMapper;
import com.learnonline.content.model.dto.SaveTeachplanDto;
import com.learnonline.content.model.dto.TeachplanDto;
import com.learnonline.content.model.po.Teachplan;
import com.learnonline.content.model.po.TeachplanMedia;
import com.learnonline.content.service.TeachplanService;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.service.impl
 * @Author: ASUS
 * @CreateTime: 2024-07-26  16:57
 * @Description: 保存课程计划
 * @Version: 1.0
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    /**
     * 保存课程计划
     *
     * @param teachplanDto 课程计划数据传输对象
     * @throws Exception 保存课程计划过程中可能出现的异常
     */
    @Override
    @Transactional
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {
        //TODO 添加service层校验注解
        Long id = teachplanDto.getId();
        //修改课程计划
        if (id != null) {
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplan.setChangeDate(LocalDateTime.now());
            int i = teachplanMapper.updateById(teachplan);
            if (i <= 0) {
                LearnOnlineException.cast("修改课程计划失败！");
            }
        } else {
            // 课程计划id为null，创建对象，拷贝属性，设置创建时间和排序号
            int count = getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            Teachplan teachplanNew = new Teachplan();
            //设置排序号
            teachplanNew.setOrderby(count + 1);
            teachplanNew.setCreateDate(LocalDateTime.now());
            BeanUtils.copyProperties(teachplanDto, teachplanNew);
            int insert = teachplanMapper.insert(teachplanNew);
            if (insert <= 0) {
                LearnOnlineException.cast("新增课程计划失败！");
            }
        }
    }

    /**
     * 删除课程计划
     *
     * @param teachplanId 课程计划ID
     * @throws LearnOnlineException 如果课程计划ID为空、当前课程计划下存在小节或删除媒资信息失败时抛出异常
     */
    @Override
    @Transactional
    public void deleteTeachplan(Long teachplanId) {
        if(teachplanId==null){
            LearnOnlineException.cast("课程计划id不能为空！");
        }
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        // select * from teachplan where parentid = {当前章计划id}
        queryWrapper.eq(Teachplan::getParentid, teachplanId);
        // 获取一下查询的条目数
        Integer count = teachplanMapper.selectCount(queryWrapper);
        // 如果当前课程计划下有小节，则抛异常
        if (count > 0) {
            LearnOnlineException.cast("当前课程计划下有小节，请先删除小节！");
        }else{
            // 课程计划下无小节，直接删除该课程计划和对应的媒资信息
            int i = teachplanMapper.deleteById(teachplanId);
            if (i <= 0) {
                LearnOnlineException.cast("删除课程计划失败！");
            }
            //条件构造器
            LambdaQueryWrapper<TeachplanMedia> mediaLambdaQueryWrapper = new LambdaQueryWrapper<>();
            mediaLambdaQueryWrapper.eq(TeachplanMedia::getTeachplanId, teachplanId);
//            //先查询对应小节课程计划中是否有媒资信息，如果有则删除
//            TeachplanMedia teachplanMedia = teachplanMediaMapper.selectById(mediaLambdaQueryWrapper);
//            if(String.valueOf(teachplanMedia).equals("null")) {
//                LearnOnlineException.cast("删除成功");
//            }
            // 删除媒资信息中对应teachplanId的数据
            teachplanMediaMapper.delete(mediaLambdaQueryWrapper);
//            if (delete <= 0) {
//                LearnOnlineException.cast("删除媒资信息失败！");
//            }
        }
    }

    /**
     * 根据移动类型和课程计划ID对课程计划进行排序
     *
     * @param moveType 移动类型，包括向上移动("moveup")和向下移动("movedown")
     * @param teachplanId 课程计划ID
     * @return 无返回值
     */
    @Override
    @Transactional
    public void orderByTeachplan(String moveType, Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        // 获取层级和当前orderby，章节移动和小节移动的处理方式不同
        Integer grade = teachplan.getGrade();
        Integer orderby = teachplan.getOrderby();
        // 章节移动是比较同一课程id下的orderby
        Long courseId = teachplan.getCourseId();
        // 小节移动是比较同一章节id下的orderby
        Long parentid = teachplan.getParentid();
        if("moveup".equals(moveType)){//向上移动类型
            if(grade==1){
                // 章节上移，找到上一个章节的orderby，然后与其交换orderby
                // SELECT * FROM teachplan WHERE courseId = 117 AND grade = 1  AND orderby < 1 ORDER BY orderby DESC LIMIT 1
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getGrade, 1)
                        .eq(Teachplan::getCourseId, courseId)
                        .lt(Teachplan::getOrderby, orderby)
                        .orderByDesc(Teachplan::getOrderby)
                        .last("LIMIT 1");
                Teachplan tmp = teachplanMapper.selectOne(queryWrapper);
                exchangeOrderby(teachplan, tmp);
            }else if(grade==2){
                // 小节上移
                // SELECT * FROM teachplan WHERE parentId = 268 AND orderby < 5 ORDER BY orderby DESC LIMIT 1
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getParentid, parentid)
                        .lt(Teachplan::getOrderby, orderby)
                        .orderByDesc(Teachplan::getOrderby)
                        .last("LIMIT 1");
                Teachplan tmp = teachplanMapper.selectOne(queryWrapper);
                exchangeOrderby(teachplan, tmp);
            }
        }else if ("movedown".equals(moveType)) {
            if (grade == 1) {
                // 章节下移
                // SELECT * FROM teachplan WHERE courseId = 117 AND grade = 1 AND orderby > 1 ORDER BY orderby ASC LIMIT 1
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getCourseId, courseId)
                        .eq(Teachplan::getGrade, grade)
                        .gt(Teachplan::getOrderby, orderby)
                        .orderByAsc(Teachplan::getOrderby)
                        .last("LIMIT 1");
                Teachplan tmp = teachplanMapper.selectOne(queryWrapper);
                exchangeOrderby(teachplan, tmp);
            } else if (grade == 2) {
                // 小节下移
                // SELECT * FROM teachplan WHERE parentId = 268 AND orderby > 1 ORDER BY orderby ASC LIMIT 1
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getParentid, parentid)
                        .gt(Teachplan::getOrderby, orderby)
                        .orderByAsc(Teachplan::getOrderby)
                        .last("LIMIT 1");
                Teachplan tmp = teachplanMapper.selectOne(queryWrapper);
                exchangeOrderby(teachplan, tmp);
            }
        }
    }

    /**
     * 交换两个课程计划的排序值
     *
     * @param teachplan 当前课程计划对象
     * @param tmp       待交换排序值的课程计划对象
     * @throws LearnOnlineException 如果待交换排序值的课程计划对象为null，则抛出异常
     */
    private void exchangeOrderby(Teachplan teachplan, Teachplan tmp) {
        if (tmp == null)
            LearnOnlineException.cast("已经到头啦，不能再移啦");
        else {
            // 交换orderby，更新
            Integer orderby = teachplan.getOrderby();
            Integer tmpOrderby = tmp.getOrderby();
            teachplan.setOrderby(tmpOrderby);
            tmp.setOrderby(orderby);
            teachplanMapper.updateById(tmp);
            teachplanMapper.updateById(teachplan);
        }
    }

    /**
     * 获取最新的排序号
     *
     * @param courseId 课程id
     * @param parentId 父课程计划id
     * @return int 最新排序号
     * @author Mr.M
     * @date 2022/9/9 13:43
     */
    private int getTeachplanCount(Long courseId, Long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }


}
