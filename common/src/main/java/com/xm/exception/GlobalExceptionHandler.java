package com.xm.exception;

import com.xm.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Tag(name = "全局异常处理", description = "统一处理系统中的各类异常")
@RestControllerAdvice
public class GlobalExceptionHandler {



    //更改密码错误异常处理
    @Operation(summary = "更改密码错误异常处理", description = "捕获并处理系统中密码更改处理的异常")
    @ExceptionHandler(PasswordChangeException.class)
    public Result passwordChangeEX(PasswordChangeException e){
        //打印堆栈中的异常信息
        e.printStackTrace();
        //响应
        return Result.error(e.getMessage());
    }



    //运行异常处理
    @Operation(summary = "更改密码错误异常处理", description = "捕获并处理系统中密码更改处理的异常")
    @ExceptionHandler(RuntimeException.class)
    public Result RuntimeEX(RuntimeException e){
        //打印堆栈中的异常信息
        e.printStackTrace();
        //响应
        return Result.error(e.getMessage());
    }


    //处理异常
    //指定需要处理的异常类型
    @Operation(summary = "通用异常处理", description = "捕获并处理系统中未被特定处理的所有异常")
    @ExceptionHandler(Exception.class)
    public Result ex(Exception e){
        //打印堆栈中的异常信息
        e.printStackTrace();
        //响应
        return Result.error("对不起,操作失败,请联系管理员");
    }
}

