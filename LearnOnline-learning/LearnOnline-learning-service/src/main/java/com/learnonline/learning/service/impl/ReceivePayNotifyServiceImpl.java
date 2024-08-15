package com.learnonline.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.learnonline.base.execption.LearnOnlineException;
import com.learnonline.learning.config.PayNotifyConfig;
import com.learnonline.learning.service.MyCourseTablesService;
import com.learnonline.messagesdk.model.po.MqMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.learning.service
 * @Author: ASUS
 * @CreateTime: 2024-08-15  08:05
 * @Description: 监听MQ，接收支付结果
 * @Version: 1.0
 */
@Slf4j
@Service
public class ReceivePayNotifyServiceImpl {

    @Autowired
    MyCourseTablesService tablesService;

    @RabbitListener(queues = PayNotifyConfig.PAYNOTIFY_QUEUE)
    public void receive(Message message) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 1. 获取消息
        MqMessage mqMessage = JSON.parseObject(message.getBody(), MqMessage.class);
        // 2. 根据我们存入的消息，进行解析
        // 2.1 消息类型，学习中心只处理支付结果的通知
        String messageType = mqMessage.getMessageType();
        // 2.2 选课id
        String chooseCourseId = mqMessage.getBusinessKey1();
        // 2.3 订单类型，60201表示购买课程，学习中心只负责处理这类订单请求
        String orderType = mqMessage.getBusinessKey2();
        // 3. 学习中心只负责处理支付结果的通知
        if (PayNotifyConfig.MESSAGE_TYPE.equals(messageType)){
            // 3.1 学习中心只负责购买课程类订单的结果
            if ("60201".equals(orderType)){
                // 3.2 保存选课记录
                boolean flag = tablesService.saveChooseCourseSuccessStatus(chooseCourseId);
                if (!flag){
                    LearnOnlineException.cast("保存选课记录失败");
                }
            }
        }
    }
}
