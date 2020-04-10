package org.nocturne.vslab.frontserver.advice;

import org.nocturne.vslab.frontserver.bean.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonControllerAdvice {

    @ExceptionHandler(value = Exception.class)
    public Result innerExceptionHandler(Exception e) {
        return new Result(-1, "内部错误", null);
    }
}
