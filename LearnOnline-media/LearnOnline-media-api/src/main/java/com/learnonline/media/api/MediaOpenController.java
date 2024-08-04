package com.learnonline.media.api;

import com.learnonline.base.execption.LearnOnlineException;
import com.learnonline.base.model.RestResponse;
import com.learnonline.media.model.po.MediaFiles;
import com.learnonline.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.media.api
 * @Author: ASUS
 * @CreateTime: 2024-08-04  16:24
 * @Description: 媒资文件管理
 * @Version: 1.0
 */
@Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
@RestController
@RequestMapping("/open")
public class MediaOpenController {
    @Autowired
    MediaFileService mediaFileService;

    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId){
        //根据mediaId查询文件信息
        MediaFiles mediaFiles = mediaFileService.getFileById(mediaId);
        if(mediaFiles==null){
            return RestResponse.validfail("找不到视频");
        }
        //取出视频播放地址
        String url = mediaFiles.getUrl();
        if(StringUtils.isEmpty(url)){
            return RestResponse.validfail("该视频正在处理中，请稍后再试");
        }
//        if(mediaFiles == null || StringUtils.isEmpty(mediaFiles.getUrl())){
//            LearnOnlineException.cast("视频还没有转码处理");
//        }
        return RestResponse.success(mediaFiles.getUrl());
    }
}
