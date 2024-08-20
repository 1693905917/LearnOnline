package com.learnonline.learning.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.learnonline.base.model.RestResponse;
import com.learnonline.base.utils.StringUtil;
import com.learnonline.content.model.po.CoursePublish;
import com.learnonline.content.model.po.Teachplan;
import com.learnonline.learning.feignclient.ContentServiceClient;
import com.learnonline.learning.feignclient.MediaServiceClient;
import com.learnonline.learning.model.dto.XcCourseTablesDto;
import com.learnonline.learning.service.LearningService;
import com.learnonline.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.learning.service.impl
 * @Author: ASUS
 * @CreateTime: 2024-08-15  18:39
 * @Description: 获取视频的接口实现方法
 * @Version: 1.0
 */
@Slf4j
@Service
public class LearningServiceImpl implements LearningService {
    @Autowired
    MyCourseTablesService courseTablesService;

    @Autowired
    ContentServiceClient contentServiceClient;

    @Autowired
    MediaServiceClient mediaServiceClient;


    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {

        //查询课程信息
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        //判断如果为null不再继续
        if(coursepublish == null){
            return RestResponse.validfail("课程不存在");
        }

        //远程调用内容管理服务根据课程计划id（teachplanId）去查询课程计划信息，如果is_preview的值为1表示支持试学
        //也可以从coursepublish对象中解析出课程计划信息去判断是否支持试学
        //如果支持试学调用媒资服务查询视频的播放地址，返回
        // 2.1 isPreview字段为1表示支持试学，返回课程url
        Teachplan teachplan = contentServiceClient.getTeachplan(teachplanId);
        if ("1".equals(teachplan.getIsPreview())) {
            return mediaServiceClient.getPlayUrlByMediaId(mediaId);
        }
        //用户已登录
        if(StringUtils.isNotEmpty(userId)){
            //获取学习资格
            XcCourseTablesDto learningStatus = courseTablesService.getLearningStatus(userId, courseId);
            //学习资格，[{"code":"702001","desc":"正常学习"},{"code":"702002","desc":"没有选课或选课后没有支付"},{"code":"702003","desc":"已过期需要申请续期或重新支付"}]
            String learnStatus = learningStatus.getLearnStatus();
            if("702002".equals(learnStatus)){
                return RestResponse.validfail("无法学习，因为没有选课或选课后没有支付");
            }else if("702003".equals(learnStatus)){
                return RestResponse.validfail("已过期需要申请续期或重新支付");
            }else{
                //有资格学习，要返回视频的播放地址
                //程调用媒资获取视频播放地址
                RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
                return playUrlByMediaId;

            }

        }
        //如果用户没有登录
        //取出课程的收费规则
        String charge = coursepublish.getCharge();
        if("201000".equals(charge)){
            //有资格学习，要返回视频的播放地址
            //远程调用媒资获取视频播放地址
            RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
            return playUrlByMediaId;
        }
        return RestResponse.validfail("课程需要购买");
    }
}