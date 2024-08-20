package com.learnonline.learning.feignclient;

import com.learnonline.base.model.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description 获取视频远程接口
 * @author Mr.M
 * @date 2022/10/27 9:04
 * @version 1.0
 */
 @FeignClient(value = "media-api",fallbackFactory = MediaServiceClientFallbackFactory.class)
 @RequestMapping("/media")
 public interface MediaServiceClient {

 /**
  * 获取媒资url
  * @param mediaId   媒资id
  * @return
  */
 @GetMapping("/open/preview/{mediaId}")
 RestResponse<String> getPlayUrlByMediaId(@PathVariable("mediaId") String mediaId);

}
