package com.learnonline.messagesdk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learnonline.messagesdk.model.po.MqMessageHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface MqMessageHistoryMapper extends BaseMapper<MqMessageHistory> {

}
