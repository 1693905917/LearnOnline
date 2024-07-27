package com.learnonline.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learnonline.base.execption.LearnOnlineException;
import com.learnonline.base.model.PageParams;
import com.learnonline.base.model.PageResult;
import com.learnonline.content.mapper.*;
import com.learnonline.content.model.dto.AddCourseDto;
import com.learnonline.content.model.dto.CourseBaseInfoDto;
import com.learnonline.content.model.dto.EditCourseDto;
import com.learnonline.content.model.dto.QueryCourseParamsDto;
import com.learnonline.content.model.po.*;
import com.learnonline.content.service.CourseBaseInfoService;
import org.apache.commons.lang.StringUtils;
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
 * @CreateTime: 2024-07-23  20:59
 * @Description: 课程信息管理业务接口实现类
 * @Version: 1.0
 */
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Autowired
    private CourseTeacherMapper courseTeacherMapper;
    @Autowired
    TeachplanMapper teachplanMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        //拼装查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //根据名称模糊查询,此时在sql中破解
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        //根据课程审核状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        //根据课程发布状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());
        //创建page分页参数对象  current:当前页，size：每页显示多少条
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        //获取分页结果
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        //获取分页结果中的数据 也就是数据列表
        List<CourseBase> items = pageResult.getRecords();
        //总记录数
        long total = pageResult.getTotal();
        //当前页
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(items, total, pageParams.getPageNo(), pageParams.getPageSize());
        return courseBasePageResult;
    }

    /**
     * 创建课程基础信息并保存课程营销信息
     *
     * @param companyId 机构ID
     * @param dto       课程添加信息DTO对象
     * @return 创建后的课程基础信息DTO对象
     * @throws RuntimeException 当课程名称为空、课程分类为空、课程等级为空、教育模式为空、适应人群为空、收费规则为空、
     *                            新增课程基本信息失败或保存课程营销信息失败时抛出异常
     */
    @Override
    @Transactional  //凡是增删改的方法都要加上事务控制
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
        //在这里我们先写一个传统的校验方式，后面我们会不用这种if判断方法来进行校验：
        //合法性校验：一定要在service层进行校验，而不是在controller
        // 校验课程名称、分类、等级、教育模式、适应人群、收费规则
        if (StringUtils.isBlank(dto.getName())) {
            //TODO 将String的中文汉字使用枚举类型取代
            throw new LearnOnlineException("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getMt())) {
            throw new LearnOnlineException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
            throw new LearnOnlineException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
            throw new LearnOnlineException("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
            throw new LearnOnlineException("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
            throw new LearnOnlineException("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
            throw new LearnOnlineException("收费规则为空");
        }

        //新增对象
        CourseBase courseBaseNew = new CourseBase();
        //将填写的课程信息赋值给新增对象
        //只要属性名称一致就可以拷贝，注意确实仅仅只是拷贝，也就是要注意dto如果有属性为空，那么courseBaseNew即使有值，也会被覆盖为空
        BeanUtils.copyProperties(dto, courseBaseNew);
        //设置审核状态 默认为未提交 202002：未提交
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态 默认为未发布 203001：未发布
        courseBaseNew.setStatus("203001");
        //机构id
        courseBaseNew.setCompanyId(companyId);
        //添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //插入课程基本信息表
        int insert = courseBaseMapper.insert(courseBaseNew);
        if (insert <= 0) {
            //我们为什么都抛RuntimeException而不抛Exception？
            //答：因为你抛Exception就要在类上再抛一次，这样对原有的代码具有可侵入性
            throw new LearnOnlineException("新增课程基本信息失败");
        }

        //向课程营销表保存课程营销信息
        //课程营销信息
        CourseMarket courseMarketNew = new CourseMarket();
        //一旦上面courseBaseMapper.insert(courseBaseNew);插入成功，courseBaseNew的Id属性就自动有了
        Long courseId = courseBaseNew.getId();
        BeanUtils.copyProperties(dto, courseMarketNew);
        courseMarketNew.setId(courseId);
        int i = saveCourseMarket(courseMarketNew);
        if(i<=0){
            throw new LearnOnlineException("保存课程营销信息失败");
        }
        //查询课程基本信息及营销信息并返回
        return getCourseBaseInfo(courseId);
    }

    /**
     * 根据课程ID获取课程基础信息DTO
     *
     * @param courseId 课程ID
     * @return 课程基础信息DTO对象，若找不到对应的课程则返回null
     */
    @Override
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId){

        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket != null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }
        //查询分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());
        return courseBaseInfoDto;
    }


    /**
     * 更新课程基础信息。
     *
     * <p>此方法用于根据提供的公司ID和课程编辑DTO（数据传输对象）来更新指定课程的基础信息。
     * 首先，根据课程ID查询课程基础信息，如果不存在则抛出异常。
     * 然后，验证当前公司是否有权修改该课程（即课程是否属于当前公司），如果不是则抛出异常。
     * 接着，将编辑DTO中的信息复制到课程基础对象中，并更新修改时间。
     * 更新课程基础信息后，如果更新操作未成功（即影响行数为0或负数），则抛出异常。
     * 同时，还会处理课程的营销信息，将其保存到数据库中。
     * 最后，重新查询并返回更新后的课程基础信息DTO。</p>
     *
     * @param companyId 公司的唯一标识符ID，用于验证课程是否属于该公司。
     * @param dto       包含要更新的课程基础信息的EditCourseDto对象。
     * @return 返回更新后的课程基础信息DTO对象。
     * @throws LearnOnlineException 如果课程不存在、当前公司无权修改该课程或更新操作失败，则抛出此异常。
     */
    @Override
    @Transactional
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto) {
        Long courseId = dto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase==null){
            LearnOnlineException.cast("课程不存在");
        }
        //根据具体的业务逻辑只能去service层校验
        //校验本机构只能修改本机构的课程
        if(!courseBase.getCompanyId().equals(companyId)){
            LearnOnlineException.cast("本机构只能修改本机构的课程");
        }

        //封装基本信息的数据
        BeanUtils.copyProperties(dto, courseBase);
        courseBase.setChangeDate(LocalDateTime.now());

        //更新课程基本信息
        int i = courseBaseMapper.updateById(courseBase);
        if(i<=0){
            LearnOnlineException.cast("更新课程基本信息失败");
        }
        //封装营销信息的数据
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto, courseMarket);
        saveCourseMarket(courseMarket);
        //查询课程信息
        return this.getCourseBaseInfo(courseId);
    }

    /**
     * 删除课程
     *
     * @param companyId 机构ID
     * @param courseId 课程ID
     * @throws LearnOnlineException 当传入的机构ID与课程所属机构ID不一致时，抛出该异常
     */
    @Override
    public void deleteCourse(Long companyId, Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(!companyId.equals(courseBase.getCompanyId())){
            LearnOnlineException.cast("只允许本机构才能删除该课程");
        }
        // 删除课程教师信息
        LambdaQueryWrapper<CourseTeacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teacherLambdaQueryWrapper.eq(CourseTeacher::getCourseId, courseId);
        courseTeacherMapper.delete(teacherLambdaQueryWrapper);
        // 删除课程计划
        LambdaQueryWrapper<Teachplan> teachplanLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teachplanLambdaQueryWrapper.eq(Teachplan::getCourseId, courseId);
        teachplanMapper.delete(teachplanLambdaQueryWrapper);
        // 删除营销信息
        courseMarketMapper.deleteById(courseId);
        // 删除课程基本信息
        courseBaseMapper.deleteById(courseId);
    }

    /**
     * 保存课程营销信息
     *
     * @param courseMarketNew 课程营销对象，包含要保存的课程营销信息
     * @return 保存结果，若成功则返回1，否则返回0
     * @throws RuntimeException 如果收费规则没有选择，或者收费规则为收费但价格没有填写或填写了非法数字，则抛出异常
     */
    //保存课程营销信息
    private int saveCourseMarket(CourseMarket courseMarketNew){
        //收费规则
        String charge = courseMarketNew.getCharge();
        if(StringUtils.isBlank(charge)){
            LearnOnlineException.cast("收费规则没有选择");
        }
        //收费规则为收费，但是价格没有填写或者填了一个非法数字
        if(charge.equals("201001")){
            if(courseMarketNew.getPrice() == null || courseMarketNew.getPrice().floatValue()<=0){
                LearnOnlineException.cast("课程为收费价格不能为空且必须大于0");
            }
        }
        //根据id从课程营销表查询
        CourseMarket courseMarketObj = courseMarketMapper.selectById(courseMarketNew.getId());
//从数据库查询营销信息，存在则更新，不存在则添加
        if(courseMarketObj == null){
            return courseMarketMapper.insert(courseMarketNew);
        }else{
            BeanUtils.copyProperties(courseMarketNew,courseMarketObj);
            courseMarketObj.setId(courseMarketNew.getId());
            return courseMarketMapper.updateById(courseMarketObj);
        }
    }

}
