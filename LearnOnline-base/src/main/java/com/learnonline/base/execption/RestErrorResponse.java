package com.learnonline.base.execption;

import java.io.Serializable;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.base.execption
 * @Author: ASUS
 * @CreateTime: 2024-07-25  20:40
 * @Description: 错误响应参数包装 相当于 和前端约定返回异常信息模型
 * @Version: 1.0
 */
public class RestErrorResponse implements Serializable {
    private String errMessage;

    public RestErrorResponse(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

}
