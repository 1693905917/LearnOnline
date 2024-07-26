package com.learnonline.base.execption;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.base.execption
 * @Author: ASUS
 * @CreateTime: 2024-07-25  20:35
 * @Description: 小志课堂项目异常类  就是自定义异常类型
 * @Version: 1.0
 */
public class LearnOnlineException extends RuntimeException {
    private String errMessage;
    public String getErrMessage() {
        return errMessage;
    }
    public LearnOnlineException() {
        super();
    }
    public LearnOnlineException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public static void cast(CommonError commonError) {
        throw new LearnOnlineException(commonError.getErrMessage());
    }

    public static void cast(String errMessage) {
        throw new LearnOnlineException(errMessage);
    }

}
