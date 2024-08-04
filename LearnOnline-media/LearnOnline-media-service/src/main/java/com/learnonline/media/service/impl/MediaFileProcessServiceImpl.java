package com.learnonline.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learnonline.media.mapper.MediaFilesMapper;
import com.learnonline.media.mapper.MediaProcessHistoryMapper;
import com.learnonline.media.mapper.MediaProcessMapper;
import com.learnonline.media.model.po.MediaFiles;
import com.learnonline.media.model.po.MediaProcess;
import com.learnonline.media.model.po.MediaProcessHistory;
import com.learnonline.media.service.MediaFileProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.media.service.impl
 * @Author: ASUS
 * @CreateTime: 2024-08-01  15:46
 * @Description: 媒资文件处理服务
 * @Version: 1.0
 */
@Slf4j
@Service
public class MediaFileProcessServiceImpl implements MediaFileProcessService {
    @Autowired
    MediaProcessMapper mediaProcessMapper;
    @Autowired
    MediaProcessHistoryMapper mediaProcessHistoryMapper;
    @Autowired
    MediaFilesMapper mediaFilesMapper;

    /**
     * 根据分片索引和总数获取媒体处理列表
     *
     * @param shardIndex 分片索引
     * @param shardTotal 分片总数
     * @param count      获取数量
     * @return 媒体处理列表
     */
    @Override
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count) {
        List<MediaProcess> mediaProcesses = mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
        return mediaProcesses;
    }

    /**
     * 启动任务
     *
     * @param id 任务的唯一标识符
     * @return 如果任务启动成功，则返回true；否则返回false
     */
    //实现如下
    public boolean startTask(long id) {
        int result = mediaProcessMapper.startTask(id);
        return result<=0?false:true;
    }

    /**
     * 保存任务完成状态
     *
     * @param taskId 任务ID
     * @param status 任务状态
     * @param fileId 文件ID
     * @param url    文件URL
     * @param errorMsg 错误信息
     * @return 无
     */
    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {

        //要更新的任务
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if(mediaProcess == null){
            return ;
        }
        //如果任务执行失败
        if(status.equals("3")){
            //更新MediaProcess表的状态
            mediaProcess.setStatus("3");
            mediaProcess.setFailCount(mediaProcess.getFailCount()+1);//失败次数加1
            mediaProcess.setErrormsg(errorMsg);
            mediaProcessMapper.updateById(mediaProcess);
            //更高效的更新方式
//            mediaProcessMapper.update()
            //todo:将上边的更新方式更改为效的更新方式
            return;

        }


        //======如果任务执行成功======
        //文件表记录
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        //更新media_file表中的url
        mediaFiles.setUrl(url);
        mediaFilesMapper.updateById(mediaFiles);

        //更新MediaProcess表的状态
        mediaProcess.setStatus("2");
        mediaProcess.setFinishDate(LocalDateTime.now());
        mediaProcess.setUrl(url);
        mediaProcessMapper.updateById(mediaProcess);

        //将MediaProcess表记录插入到MediaProcessHistory表
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess,mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);

        //从MediaProcess删除当前任务
        mediaProcessMapper.deleteById(taskId);
    }
}
