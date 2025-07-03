package com.xm.exception;

import com.xm.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //处理异常
    //指定需要处理的异常类型
    @ExceptionHandler(Exception.class)
    public Result ex(Exception e){
        //打印堆栈中的异常信息
        e.printStackTrace();
        //响应
        return Result.error("对不起,操作失败,请联系管理员");
    }
}
