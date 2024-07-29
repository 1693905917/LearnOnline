package com.learnonline.media.service;

import com.learnonline.base.model.PageParams;
import com.learnonline.base.model.PageResult;
import com.learnonline.media.model.dto.QueryMediaParamsDto;
import com.learnonline.media.model.dto.UploadFileParamsDto;
import com.learnonline.media.model.dto.UploadFileResultDto;
import com.learnonline.media.model.po.MediaFiles;


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
 public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);


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
}
