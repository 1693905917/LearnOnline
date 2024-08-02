package com.learnonline.media.service;

import com.learnonline.media.model.po.MediaProcess;

import java.util.List;

public interface MediaFileProcessService {
    /**
     * 获取待处理任务列表
     *
     * @param shardIndex 分片序号，用于分布式处理时确定处理任务的分片范围
     * @param shardTotal 分片总数，用于分布式处理时确定处理任务的总分片数
     * @param count      获取记录数，用于限制每次获取待处理任务的数量
     * @return 返回一个待处理任务列表
     */
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

    /**
     *  开启一个任务
     * @param id 任务id
     * @return true开启任务成功，false开启任务失败
     */
    public boolean startTask(long id);

    /**
     * @description 保存任务结果
     * @param taskId  任务id
     * @param status 任务状态
     * @param fileId  文件id
     * @param url url
     * @param errorMsg 错误信息
     */
    void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);

}
