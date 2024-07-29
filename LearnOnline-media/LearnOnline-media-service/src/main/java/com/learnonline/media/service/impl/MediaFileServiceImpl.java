package com.learnonline.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.learnonline.base.execption.LearnOnlineException;
import com.learnonline.base.model.PageParams;
import com.learnonline.base.model.PageResult;
import com.learnonline.media.mapper.MediaFilesMapper;
import com.learnonline.media.model.dto.QueryMediaParamsDto;
import com.learnonline.media.model.dto.UploadFileParamsDto;
import com.learnonline.media.model.dto.UploadFileResultDto;
import com.learnonline.media.model.po.MediaFiles;
import com.learnonline.media.service.MediaFileService;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @description 上传文件
 * @author Mr.M
 * @date 2022/9/10 8:58
 * @version 1.0
 */
@Slf4j
 @Service
public class MediaFileServiceImpl implements MediaFileService {

 @Autowired
 MinioClient minioClient;

 @Autowired
  MediaFilesMapper mediaFilesMapper;

 @Autowired
 MediaFileService currentProxy;

 //存储普通文件桶
 @Value("${minio.bucket.files}")
 private String bucket_files;
 //存储视频桶
 @Value("${minio.bucket.videofiles}")
 private String bucket_video;


 @Override
 public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

  //构建查询条件对象
  LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
  
  //分页对象
  Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
  // 查询数据内容获得结果
  Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
  // 获取数据列表
  List<MediaFiles> list = pageResult.getRecords();
  // 获取数据总数
  long total = pageResult.getTotal();
  // 构建结果集
  PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
  return mediaListResult;

 }

 //获取文件默认存储目录路径 年/月/日
//我们规定在桶里面的存储规则是以年月日来进行划分存储
 private String getDefaultFolderPath() {
//先格式化时间为年/月/日
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//将当前时间的“-”替换成”/”，后面再加一个“/”
  String folder = sdf.format(new Date()).replace("-", "/")+"/";
  return folder;
 }

 //获取文件的md5
 private String getFileMd5(File file) {
  try (FileInputStream fileInputStream = new FileInputStream(file)) {
   String fileMd5 = DigestUtils.md5Hex(fileInputStream);
   return fileMd5;
  } catch (Exception e) {
   e.printStackTrace();
   return null;
  }
 }

 //根据扩展名取出mimeType
 private String getMimeType(String extension){
  if(extension==null)
   extension = "";
  //根据扩展名取出mimeType
  ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
  //通用mimeType，字节流
  String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
  if(extensionMatch!=null){
   mimeType = extensionMatch.getMimeType();
  }
  return mimeType;
 }
 /**
  * @description 将文件写入minIO
  * @param localFilePath  文件地址
  * @param bucket  桶
  * @param objectName 对象名称
  * @return void
  */
 public boolean addMediaFilesToMinIO(String localFilePath,String mimeType,String bucket, String objectName) {
  try {
   UploadObjectArgs testbucket = UploadObjectArgs.builder()
           .bucket(bucket)//桶
           .object(objectName)//指定本地文件路径
           .filename(localFilePath)//对象名
           .contentType(mimeType)//设置媒体文件类型
           .build();
   minioClient.uploadObject(testbucket);
   log.debug("上传文件到minio成功,bucket:{},objectName:{}",bucket,objectName);
   System.out.println("上传成功");
   return true;
  } catch (Exception e) {
   e.printStackTrace();
   log.error("上传文件到minio出错,bucket:{},objectName:{},错误原因:{}",bucket,objectName,e.getMessage(),e);
   LearnOnlineException.cast("上传文件到文件系统失败");
  }
  return false;
 }

 /**
  * @description 将文件信息添加到文件表
  * @param companyId  机构id
  * @param fileMd5  文件md5值
  * @param uploadFileParamsDto  上传文件的信息
  * @param bucket  桶
  * @param objectName 对象名称
  * @return com.xuecheng.media.model.po.MediaFiles
  */
 @Transactional
 public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName){
  //从数据库查询文件,之前是否有存储过该文件
  MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
  if (mediaFiles == null) {
   mediaFiles = new MediaFiles();
   //拷贝基本信息
   BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
   mediaFiles.setId(fileMd5);//文件ID
   mediaFiles.setFileId(fileMd5);
   mediaFiles.setCompanyId(companyId);//机构ID
   mediaFiles.setUrl("/" + bucket + "/" + objectName);
   mediaFiles.setBucket(bucket);
   mediaFiles.setFilePath(objectName);//对应数据库的file_path字段
   mediaFiles.setCreateDate(LocalDateTime.now());
   mediaFiles.setAuditStatus("002003");//审核状态：省去人工审核
   mediaFiles.setStatus("1");//状态 1：正常 0：不展示
   //保存文件信息到文件表
   int insert = mediaFilesMapper.insert(mediaFiles);
   int a=1/0;
   if (insert < 0) {
    log.error("保存文件信息到数据库失败,{}",mediaFiles.toString());
    LearnOnlineException.cast("保存文件信息失败");
   }
   log.debug("保存文件信息到数据库成功,{}",mediaFiles.toString());

  }
  return mediaFiles;

 }
 /**
  * 上传文件到MinIO并保存到数据库
  *
  * @param companyId 公司ID
  * @param uploadFileParamsDto 上传文件参数
  * @param localFilePath 本地文件路径
  * @return 上传文件结果
  * @throws LearnOnlineException 如果文件不存在、上传文件失败或保存信息失败
  */
// @Transactional
 @Override
 public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath) {
  File file = new File(localFilePath);
  if (!file.exists()) {
   LearnOnlineException.cast("文件不存在");
  }
  //获取文件名称，文件名称中有扩展名
  String filename = uploadFileParamsDto.getFilename();
  //获取文件扩展名
  String extension = filename.substring(filename.lastIndexOf("."));
  //文件mimeType
  String mimeType = getMimeType(extension);
  //文件的md5值
  String fileMd5 = getFileMd5(file);
  //文件的默认目录
  String defaultFolderPath = getDefaultFolderPath();
  //存储到minio中的对象名(带目录)
//由于文件名不能重复所以以当前文件的MD5值为区别标志
  String  objectName = defaultFolderPath + fileMd5 + extension;
  //将文件上传到minio
  boolean result = addMediaFilesToMinIO(localFilePath, mimeType, bucket_files, objectName);
  if(!result){
   LearnOnlineException.cast("上传文件失败");
  }
  //文件大小
  uploadFileParamsDto.setFileSize(file.length());
  //将文件信息存储到数据库
  MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_files, objectName);
  if(mediaFiles==null){
   LearnOnlineException.cast("上传文件后保存信息失败");
  }
  //准备返回数据
  UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
  BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
  return uploadFileResultDto;
 }

}
