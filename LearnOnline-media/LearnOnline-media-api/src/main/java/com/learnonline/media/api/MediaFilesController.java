package com.learnonline.media.api;

import com.learnonline.base.model.PageParams;
import com.learnonline.base.model.PageResult;
import com.learnonline.media.model.dto.QueryMediaParamsDto;
import com.learnonline.media.model.dto.UploadFileParamsDto;
import com.learnonline.media.model.dto.UploadFileResultDto;
import com.learnonline.media.model.po.MediaFiles;
import com.learnonline.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @version 1.0
 * @description 媒资文件管理接口
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {


    @Autowired
    MediaFileService mediaFileService;


    /**
     * 媒资列表查询接口
     *
     * @param pageParams 分页参数
     * @param queryMediaParamsDto 查询媒体文件参数
     * @return 返回媒体文件列表的分页结果
     */
    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);

    }

    /**
     * 上传文件
     *
     * @param filedata 上传的文件
     * @return 上传文件的结果
     * @throws IOException 抛出IO异常
     */
    @ApiOperation("上传文件")
// 指定传递的类型：MediaType.MULTIPART_FORM_DATA_VALUE
    @RequestMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//@RequestPart:获取前端表格中文件上传对应的name
    public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile filedata) throws IOException {
        Long companyId = 1232141425L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        //文件大小
        uploadFileParamsDto.setFileSize(filedata.getSize());
        //文件类型：图片
        uploadFileParamsDto.setFileType("001001");
        //获取文件原始名称
        uploadFileParamsDto.setFilename(filedata.getOriginalFilename());    //获取文件大小
        long fileSize = filedata.getSize();
        uploadFileParamsDto.setFileSize(fileSize);
        //由于我们不可能获取到用户文件的本地路径，所以我们先在项目中创建临时文件：前缀是minio  后缀是temp
        File tempFile = File.createTempFile("minio", "temp");
        //然后将上传的文件拷贝到临时文件
        filedata.transferTo(tempFile);
        //就可以获取文件路径
        String localFilePath = tempFile.getAbsolutePath();
        //上传文件
        UploadFileResultDto uploadFileResultDto = mediaFileService.uploadFile(companyId, uploadFileParamsDto, localFilePath);

        return uploadFileResultDto;

    }

}
