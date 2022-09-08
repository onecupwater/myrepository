package com.douyiyuan.first.common.exception;

import com.douyiyuan.first.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ServiceException.class)
    @ResponseBody
    public Result exceptionHandler(ServiceException se){
        return Result.error(se.getCode(),se.getMessage());
    }
}
