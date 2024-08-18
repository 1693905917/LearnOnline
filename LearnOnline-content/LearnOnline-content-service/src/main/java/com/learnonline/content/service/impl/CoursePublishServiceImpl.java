package com.learnonline.content.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.learnonline.base.execption.CommonError;
import com.learnonline.base.execption.LearnOnlineException;
import com.learnonline.content.config.MultipartSupportConfig;
import com.learnonline.content.feignclient.MediaServiceClient;
import com.learnonline.content.mapper.CourseBaseMapper;
import com.learnonline.content.mapper.CourseMarketMapper;
import com.learnonline.content.mapper.CoursePublishMapper;
import com.learnonline.content.mapper.CoursePublishPreMapper;
import com.learnonline.content.model.dto.CourseBaseInfoDto;
import com.learnonline.content.model.dto.CoursePreviewDto;
import com.learnonline.content.model.dto.TeachplanDto;
import com.learnonline.content.model.po.CourseBase;
import com.learnonline.content.model.po.CourseMarket;
import com.learnonline.content.model.po.CoursePublish;
import com.learnonline.content.model.po.CoursePublishPre;
import com.learnonline.content.service.CourseBaseInfoService;
import com.learnonline.content.service.CoursePublishService;
import com.learnonline.content.service.TeachplanService;

import com.learnonline.messagesdk.model.po.MqMessage;
import com.learnonline.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.content.service.impl
 * @Author: ASUS
 * @CreateTime: 2024-08-04  15:53
 * @Description: 课程发布服务实现
 * @Version: 1.0
 */
@Slf4j
@Service
public class CoursePublishServiceImpl implements CoursePublishService {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Autowired
    TeachplanService teachplanService;

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    MediaServiceClient mediaServiceClient;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 获取课程预览信息
     *
     * @param courseId 课程ID
     * @return 返回课程预览信息的DTO对象
     */
    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        //课程基本信息、营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        //课程计划信息
        List<TeachplanDto> teachplanTree= teachplanService.findTeachplanTree(courseId);

        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanTree);
        return coursePreviewDto;
    }

    /**
     * 提交课程审核
     *
     * @param companyId 机构ID
     * @param courseId  课程ID
     * @throws LearnOnlineException 提交课程审核时可能抛出的异常
     * @Transactional 注解表示该方法是一个事务方法，保证数据的一致性
     */
    @Transactional
    @Override
    public void commitAudit(Long companyId, Long courseId) {
        //约束校验
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        //课程审核状态
        String auditStatus = courseBase.getAuditStatus();
        //当前审核状态为已提交不允许再次提交
        if("202003".equals(auditStatus)){
            LearnOnlineException.cast("当前为等待审核状态，审核完成可以再次提交。");
        }
        //本机构只允许提交本机构的课程
        if(!courseBase.getCompanyId().equals(companyId)){
            LearnOnlineException.cast("不允许提交其它机构的课程。");
        }

        //课程图片是否填写
        if(StringUtils.isEmpty(courseBase.getPic())){
            LearnOnlineException.cast("提交失败，请上传课程图片");
        }

        //添加课程预发布记录
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        //课程基本信息加部分营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        BeanUtils.copyProperties(courseBaseInfo,coursePublishPre);
        //课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //转为json
        String courseMarketJson = JSON.toJSONString(courseMarket);
        //将课程营销信息json数据放入课程预发布表
        coursePublishPre.setMarket(courseMarketJson);

        //查询课程计划信息
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        if(teachplanTree.size()<=0||teachplanTree==null){
            LearnOnlineException.cast("提交失败，还没有添加课程计划");
        }
        //转json
        String teachplanTreeString = JSON.toJSONString(teachplanTree);
        coursePublishPre.setTeachplan(teachplanTreeString);

        //设置预发布记录状态,已提交
        coursePublishPre.setStatus("202003");
        //教学机构id
        coursePublishPre.setCompanyId(companyId);
        //提交时间
        coursePublishPre.setCreateDate(LocalDateTime.now());
        CoursePublishPre coursePublishPreUpdate = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPreUpdate == null){
            //添加课程预发布记录
            coursePublishPreMapper.insert(coursePublishPre);
        }else{
            coursePublishPreMapper.updateById(coursePublishPre);
        }
        //更新课程基本表的审核状态
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);
    }

    /**
     * 发布课程
     *
     * @param companyId 机构ID
     * @param courseId  课程ID
     * @throws LearnOnlineException 抛出自定义异常，用于处理发布课程时出现的错误情况
     * @Transactional 注解表明该方法是一个事务方法，在执行过程中发生异常会自动回滚
     */
    @Transactional
    @Override
    public void publish(Long companyId, Long courseId) {
        //约束校验
        //查询课程预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPre == null){
            LearnOnlineException.cast("请先提交课程审核，审核通过才可以发布");
        }
        //本机构只允许提交本机构的课程
        if(!coursePublishPre.getCompanyId().equals(companyId)){
            LearnOnlineException.cast("不允许提交其它机构的课程。");
        }

        //课程审核状态
        String auditStatus = coursePublishPre.getStatus();
        //审核通过方可发布
        if(!"202004".equals(auditStatus)){
            LearnOnlineException.cast("操作失败，课程审核通过方可发布。");
        }
        //保存课程发布信息
        saveCoursePublish(courseId);

        //保存消息表
        saveCoursePublishMessage(courseId);

        //删除课程预发布表对应记录
        coursePublishPreMapper.deleteById(courseId);
    }

    /**
     * 生成课程Html文件
     *
     * @param courseId 课程ID
     * @return 生成的Html文件
     * @throws LearnOnlineException 当课程静态化异常时，抛出异常
     */
    @Override
    public File generateCourseHtml(Long courseId) {
        //静态化文件
        File htmlFile  = null;
        try {
            //配置freemarker
            Configuration configuration = new Configuration(Configuration.getVersion());

            //加载模板
            //选指定模板路径,classpath下templates下
            //得到classpath路径
            String classpath = this.getClass().getResource("/").getPath();
            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            //设置字符编码
            configuration.setDefaultEncoding("utf-8");

            //指定模板文件名称
            Template template = configuration.getTemplate("course_template.ftl");

            //准备数据
            CoursePreviewDto coursePreviewInfo = this.getCoursePreviewInfo(courseId);

            Map<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewInfo);

            //静态化
            //参数1：模板，参数2：数据模型
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
//            System.out.println(content);
            //将静态化内容输出到文件中
            InputStream inputStream = IOUtils.toInputStream(content);
            //创建静态化文件
            htmlFile = File.createTempFile("course",".html");
            log.debug("课程静态化，生成静态文件:{}",htmlFile.getAbsolutePath());
            //输出流
            FileOutputStream outputStream = new FileOutputStream(htmlFile);
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.error("课程静态化异常:{}",e.toString());
            LearnOnlineException.cast("课程静态化异常");
        }
        return htmlFile;
    }

    /**
     * 上传课程Html文件
     *
     * @param courseId 课程ID
     * @param file     需要上传的文件
     * @throws LearnOnlineException 当上传文件失败时，抛出异常
     */
    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        String course = mediaServiceClient.uploadFile(multipartFile, "course/"+courseId+".html");
        if(course==null){
            LearnOnlineException.cast("上传静态文件异常");
        }

    }

    /**
     * 根据课程ID获取课程发布信息
     *
     * @param courseId 课程ID
     * @return 返回课程发布信息，若未找到则返回null
     */
    @Override
    public CoursePublish getCoursePublish(Long courseId) {
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        return coursePublish ;

    }

    @Override
    public CoursePublish getCoursePublishCache(Long courseId) {

        //查询缓存
        Object  jsonObj = redisTemplate.opsForValue().get("course:" + courseId);
        if(jsonObj!=null){
            String jsonString = jsonObj.toString();
            if(jsonString.equals("null"))
                return null;
            CoursePublish coursePublish = JSON.parseObject(jsonString, CoursePublish.class);
            return coursePublish;
        } else {
            //从数据库查询
            System.out.println("从数据库查询数据...");
            CoursePublish coursePublish = getCoursePublish(courseId);
            //设置过期时间300秒，如果是null最多缓存30s，因为哪天这个Id有了，你还是null就又出bug了
            redisTemplate.opsForValue().set("course:" + courseId, JSON.toJSONString(coursePublish),30, TimeUnit.SECONDS);
            return coursePublish;
        }
    }

//    public CoursePublish getCoursePublishCache(Long courseId) {
//        // 1. 先从缓存中查询
//        String courseCacheJson = (String) redisTemplate.opsForValue().get("course:" + courseId);
//        // 2. 如果缓存里有，直接返回
//        if (StringUtils.isNotEmpty(courseCacheJson)) {
//            log.debug("从缓存中查询");
//            CoursePublish coursePublish = JSON.parseObject(courseCacheJson, CoursePublish.class);
//            return coursePublish;
//        } else {
//            log.debug("缓存中没有，查询数据库");
//            // 3. 如果缓存里没有，查询数据库
//            CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
//            String jsonString = JSON.toJSONString(coursePublish);
//            // 3.1 将查询结果缓存
//            redisTemplate.opsForValue().set("course:" + courseId, jsonString);
//            // 3.1 返回查询结果
//            return coursePublish;
//        }
//    }

    /**
     * @description 保存课程发布信息
     * @param courseId  课程id
     * @return void
     */
    private void saveCoursePublish(Long courseId){
        //整合课程发布信息
        //查询课程预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPre == null){
            LearnOnlineException.cast("课程预发布数据为空");
        }

        CoursePublish coursePublish = new CoursePublish();

        //拷贝到课程发布对象
        BeanUtils.copyProperties(coursePublishPre,coursePublish);
        coursePublish.setStatus("203002");
        CoursePublish coursePublishUpdate = coursePublishMapper.selectById(courseId);
        if(coursePublishUpdate == null){
            coursePublishMapper.insert(coursePublish);
        }else{
            coursePublishMapper.updateById(coursePublish);
        }
        //更新课程基本表的发布状态
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);

    }

    /**
     * @description 保存消息表记录，稍后实现
     * @param courseId  课程id
     * @return void
     */
    private void saveCoursePublishMessage(Long courseId){
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if(mqMessage==null){
            LearnOnlineException.cast(CommonError.UNKOWN_ERROR);
        }
    }


}
