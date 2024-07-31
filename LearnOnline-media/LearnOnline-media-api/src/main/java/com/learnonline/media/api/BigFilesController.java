package com.learnonline.media.api;

import com.learnonline.base.model.RestResponse;
import com.learnonline.media.model.dto.UploadFileParamsDto;
import com.learnonline.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.media.api
 * @Author: ASUS
 * @CreateTime: 2024-07-30  15:00
 * @Description: 大文件上传接口
 * @Version: 1.0
 */
@Api(value = "大文件上传接口", tags = "大文件上传接口")
@RestController
public class BigFilesController {
    @Autowired
    MediaFileService mediaFileService;

    /**
     * 文件上传前检查文件
     *
     * @param fileMd5 文件MD5值
     * @return 返回检查结果，true表示文件已存在，false表示文件不存在
     * @throws Exception 异常信息
     */
    @ApiOperation(value = "文件上传前检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkfile(@RequestParam("fileMd5") String fileMd5) throws Exception {
        return mediaFileService.checkFile(fileMd5);
    }


    /**
     * 分块文件上传前的检测
     *
     * @param fileMd5 文件MD5值
     * @param chunk   文件分块编号
     * @return 返回检查结果，true表示分块已存在，false表示分块不存在
     * @throws Exception 异常信息
     */
    @ApiOperation(value = "分块文件上传前的检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk) throws Exception {
        return mediaFileService.checkChunk(fileMd5,chunk);
    }

    /**
     * 上传分块文件
     *
     * @param file 分块文件，类型为MultipartFile
     * @param fileMd5 文件MD5值，用于校验文件完整性
     * @param chunk 文件分块编号，标识上传的分块
     * @return 返回RestResponse类型，包含上传结果
     * @throws Exception 上传过程中出现异常
     */
    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadchunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws Exception {
        //创建临时文件
        File tempFile = File.createTempFile("minio", "temp");
        //上传的文件拷贝到临时文件
        file.transferTo(tempFile);
        //文件路径
        String localFilePath = tempFile.getAbsolutePath();
        return mediaFileService.uploadChunk(fileMd5,chunk,localFilePath);
    }

    /**
     * 合并文件
     *
     * @param fileMd5    文件MD5值
     * @param fileName   文件名称
     * @param chunkTotal 分块总数
     * @return RestResponse 合并结果
     * @throws Exception 合并文件时可能发生的异常
     */
    //TODO 分块文件清理问题:上传一个文件进行分块上传，上传一半不传了，之前上传到minio的分块文件要清理吗？怎么做的？
    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse mergechunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) throws Exception {
        Long companyId = 1232141425L;

        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFileType("001002");//上传的类型：视频
        uploadFileParamsDto.setTags("课程视频");
        uploadFileParamsDto.setRemark("");
        uploadFileParamsDto.setFilename(fileName);

        return mediaFileService.mergechunks(companyId,fileMd5,chunkTotal,uploadFileParamsDto);
    }

}
