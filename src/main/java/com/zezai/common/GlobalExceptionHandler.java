package com.zezai.common;
/*全局异常处理*/

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})    //表示拦截带有RestController注解的controller
@ResponseBody
public class GlobalExceptionHandler {

    //异常处理方法
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException exception){
        //如果异常信息包含Duplicate entry关键字
        if(exception.getMessage().contains("Duplicate entry")){
            String[] split=exception.getMessage().split(" "); //用数组存放异常信息
            String msg=split[2]+"已存在";   //取出报错的账号索引
            return Result.error(msg);
        }
        return Result.error("未知错误");
    }

    //自定义的异常处理方法
    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException exception){

        return Result.error(exception.getMessage());
    }
}
