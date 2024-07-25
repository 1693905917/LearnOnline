package com.learnonline.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.base.model
 * @Author: ASUS
 * @CreateTime: 2024-07-23  16:23
 * @Description: 分页查询通用参数
 * @Version: 1.0
 */
@Data
@ToString
public class PageParams {

    //当前页码
    @ApiModelProperty("当前页码")
    private Long pageNo = 1L;

    //每页记录数默认值
    @ApiModelProperty("每页记录数默认值")
    private Long pageSize =10L;

    public PageParams(){

    }

    public PageParams(long pageNo,long pageSize){
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

}
