package com.learnonline.media.service;

import com.learnonline.base.model.PageParams;
import com.learnonline.base.model.PageResult;
import com.learnonline.base.model.RestResponse;
import com.learnonline.media.model.dto.QueryMediaParamsDto;
import com.learnonline.media.model.dto.UploadFileParamsDto;
import com.learnonline.media.model.dto.UploadFileResultDto;
import com.learnonline.media.model.po.MediaFiles;

import java.io.File;


/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService {

 /**
  * @description 媒资文件查询方法
  * @param pageParams 分页参数
  * @param queryMediaParamsDto 查询条件
  * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
  * @author Mr.M
  * @date 2022/9/10 8:57
 */
 public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);


 /**
  * 上传文件
  * @param companyId 机构id
  * @param uploadFileParamsDto 上传文件信息
  * @param localFilePath 文件磁盘路径：用户的本地文件路径
  * @return 文件信息
  */
 public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath);


 /**
  * 将媒体文件信息添加到数据库中
  *
  * @param companyId 公司ID
  * @param fileMd5 文件MD5值
  * @param uploadFileParamsDto 上传文件参数
  * @param bucket 存储桶名称
  * @param objectName 对象名称
  * @return MediaFiles 媒体文件信息对象
  */
 public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName);


 /**
  * 检查文件是否存在
  *
  * @param fileMd5 文件的md5值
  * @return 返回一个RestResponse对象，包含文件是否存在的布尔值。false表示文件不存在，true表示文件存在
  */
 public RestResponse<Boolean> checkFile(String fileMd5);

 /**
  * 检查分块是否存在
  *
  * @param fileMd5  文件的md5值
  * @param chunkIndex 分块序号
  * @return 返回一个RestResponse对象，包含分块是否存在的布尔值。false表示分块不存在，true表示分块存在
  */
 public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);


 /**
  * @description 上传分块
  * @param fileMd5  文件md5
  * @param chunk  分块序号
  * @param localChunkFilePath  文件字节
  * @return com.xuecheng.base.model.RestResponse
  */
 public RestResponse uploadChunk(String fileMd5, int chunk,String localChunkFilePath);

 /**
  * @description 合并分块
  * @param companyId  机构id
  * @param fileMd5  文件md5
  * @param chunkTotal 分块总和
  * @param uploadFileParamsDto 文件信息
  * @return com.xuecheng.base.model.RestResponse
  *记录companyId  机构id是为了当机构如果传的视频过多，我们平台就要开始收费了，所以标记这些视频是哪个机构传的
  */
 public RestResponse mergechunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto);


 /**
  * 从minio下载文件
  * @param bucket 桶
  * @param objectName 对象名称
  * @return 下载后的文件
  */
 public File downloadFileFromMinIO(String bucket, String objectName);

 /**
  * @param localFilePath 文件地址
  * @param bucket        桶
  * @param objectName    对象名称
  * @return void
  * @description 将文件写入minIO
  */
 public boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName);
}
