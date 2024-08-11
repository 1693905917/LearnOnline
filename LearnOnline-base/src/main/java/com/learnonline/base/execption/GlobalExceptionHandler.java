package com.learnonline.base.execption;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.base.execption
 * @Author: ASUS
 * @CreateTime: 2024-07-25  20:43
 * @Description: 全局异常处理器
 * @Version: 1.0
 */
@Slf4j
@ControllerAdvice //通过增强类把我们遇到的异常信息捕获，基于AOP的原理对controller进行增强
public class GlobalExceptionHandler {

    /**
     * 自定义异常处理
     * <p>
     * 该方法用于捕获并处理项目中出现的{@link LearnOnlineException}异常，当发生此类异常时，会返回内部服务器错误（500）给客户端，
     * 并记录异常信息到日志中。
     * </p>
     *
     * @param e {@link LearnOnlineException}类型的异常对象，包含了项目中出现的异常信息
     * @return {@link RestErrorResponse}对象，包含了异常的详细信息，用于返回给前端
     */
    @ResponseBody
    @ExceptionHandler(LearnOnlineException.class)
    //@ResponseStatus用于指定处理 HTTP 请求时返回的 HTTP 状态码
    //HttpStatus.INTERNAL_SERVER_ERROR表示一个通用的错误响应，意味着服务器遇到了一个意外的情况，导致它无法完成请求。
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(LearnOnlineException e) {//LearnOnlineException e里面自动存储项目中出现的异常信息
        log.error("【系统异常】{}", e.getErrMessage(),e);//记录异常
        //解析出异常信息
        return new RestErrorResponse(e.getMessage());//返回错误信息给前端
    }

    /**
     * 全局异常处理:简单来说  除了我们自定义异常处理，还有其他异常处理，通通交给下面这个方法
     * <p>
     * 该方法用于捕获并处理所有未被其他异常处理器捕获的异常。当发生未知异常时，会返回内部服务器错误（500）给客户端，
     * 并记录异常信息到日志中。该方法通过注解@ExceptionHandler(Exception.class)指定其处理的异常类型为所有异常（Exception.class）。
     * </p>
     *
     * @param e 捕获到的异常对象
     * @return {@link RestErrorResponse}对象，包含了统一的错误信息，用于返回给前端
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e){
        log.error("【系统异常】{}",e.getMessage(),e);
        e.printStackTrace();
        if(e.getMessage().equals("不允许访问")){
            return new RestErrorResponse("没有操作此功能的权限");
        }
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());

    }

    /**
     * 处理方法参数验证失败异常
     *
     * 当请求的方法参数不满足验证规则时，此方法会被调用。
     * 它首先获取所有字段的验证错误信息，然后将这些错误信息拼接成一个字符串，
     * 并记录到日志中。最后，它返回一个包含错误信息的RestErrorResponse对象。
     *
     * @param e 方法参数验证失败的异常对象
     * @return 包含错误信息的RestErrorResponse对象
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
//你可能错误信息不至一个，比如你名称没填，内容介绍也没写，所以MethodArgumentNotValidException e可能会有多个错误提示信息
        List<String> msgList = new ArrayList<>();//将错误信息放在msgList

        bindingResult.getFieldErrors().stream().forEach(item->msgList.add(item.getDefaultMessage()));
        //拼接错误信息
        String msg = StringUtils.join(msgList, ",");
        log.error("【系统异常】{}",msg);
        return new RestErrorResponse(msg);
    }


}
