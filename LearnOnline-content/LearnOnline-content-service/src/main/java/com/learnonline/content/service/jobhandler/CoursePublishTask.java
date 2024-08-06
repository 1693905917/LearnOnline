package com.learnonline.content.service.jobhandler;

import com.learnonline.base.execption.LearnOnlineException;
import com.learnonline.content.feignclient.CourseIndex;
import com.learnonline.content.feignclient.SearchServiceClient;
import com.learnonline.content.mapper.CoursePublishMapper;
import com.learnonline.content.model.po.CoursePublish;
import com.learnonline.content.service.CoursePublishService;
import com.learnonline.messagesdk.model.po.MqMessage;
import com.learnonline.messagesdk.service.MessageProcessAbstract;
import com.learnonline.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.service.jobhandler
 * @Author: ASUS
 * @CreateTime: 2024-08-05  20:22
 * @Description:  课程发布XXLJob任务
 * @Version: 1.0
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    @Autowired
    CoursePublishService coursePublishService;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @Autowired
    SearchServiceClient searchServiceClient;
    /**
     * 任务调度入口，用于处理课程发布任务
     *
     * @throws Exception 抛出异常
     *
     * @XxlJob("CoursePublishJobHandler") 标记为XXL-JOB的定时任务，执行器名为"CoursePublishJobHandler"
     */
    //任务调度入口
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex="+shardIndex+",shardTotal="+shardTotal);
        //参数:分片序号、分片总数、消息类型、一次最多取到的任务数量、一次任务调度执行的超时时间
        process(shardIndex,shardTotal,"course_publish",30,60);
    }

    /**
     * 课程发布任务处理
     *
     * @param mqMessage 消息对象
     * @return 是否执行成功
     */
    //课程发布任务处理
    @Override
    public boolean execute(MqMessage mqMessage) {
        //获取消息相关的业务信息
        String businessKey1 = mqMessage.getBusinessKey1();
        long courseId = Integer.parseInt(businessKey1);
        //课程静态化
        generateCourseHtml(mqMessage,courseId);
        //课程索引
        saveCourseIndex(mqMessage,courseId);
        //课程缓存
        //saveCourseCache(mqMessage,courseId);
        return true;
    }


    /**
     * 生成课程静态化页面并上传至文件系统
     *
     * @param mqMessage 消息对象
     * @param courseId  课程ID
     * @throws LearnOnlineException 当课程静态化失败时抛出异常
     */
    //生成课程静态化页面并上传至文件系统
    public void generateCourseHtml(MqMessage mqMessage,long courseId){
        log.debug("开始进行课程静态化,课程id:{}",courseId);
        //消息id
        Long id = mqMessage.getId();
        //消息处理的service
        MqMessageService mqMessageService = this.getMqMessageService();
        //消息幂等性处理
        int stageOne = mqMessageService.getStageOne(id);
        if(stageOne >0){
            log.debug("课程静态化已处理,无需处理...，课程id:{}",courseId);
            return ;
        }
        //生成静态化页面
        File file = coursePublishService.generateCourseHtml(courseId);
        //上传静态化页面
        if(file!=null){
            coursePublishService.uploadCourseHtml(courseId,file);
        }else{
            log.debug("课程静态化失败,课程id:{}",courseId);
            LearnOnlineException.cast("课程静态化失败");
        }
        //保存第一阶段状态
        mqMessageService.completedStageOne(id);

    }

    /**
     * 保存课程索引信息
     *
     * @param mqMessage 消息对象
     * @param courseId  课程ID
     * @return 无返回值
     */
    //保存课程索引信息
    public void saveCourseIndex(MqMessage mqMessage,long courseId){
        log.debug("保存课程索引信息,课程id:{}",courseId);
        //消息id
        Long id = mqMessage.getId();
        //消息处理的service
        MqMessageService mqMessageService = this.getMqMessageService();
        //消息幂等性处理
        int stageTwo = mqMessageService.getStageTwo(id);
        if(stageTwo >0){
            log.debug("课程索引信息已写入,无需处理...，课程id:{}",courseId);
            return ;
        }
        Boolean result = saveCourseIndex(courseId);
        if(result){
            //保存第一阶段状态
            mqMessageService.completedStageTwo(id);
        }else{
            log.debug("课程索引信息写入失败,课程id:{}",courseId);
            LearnOnlineException.cast("课程索引信息写入失败");
        }
    }

    private Boolean saveCourseIndex(Long courseId) {
        //取出课程发布信息
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        //拷贝至课程索引对象
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish,courseIndex);
        //远程调用搜索服务api添加课程信息到索引
        Boolean add = searchServiceClient.add(courseIndex);
        if(!add){
            LearnOnlineException.cast("添加索引失败");
        }
        return add;
    }


    //将课程信息缓存至redis
    public void saveCourseCache(MqMessage mqMessage,long courseId){
        log.debug("将课程信息缓存至redis,课程id:{}",courseId);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
