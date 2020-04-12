package org.nocturne.vslab.frontserver.advice;

import org.nocturne.vslab.frontserver.bean.Result;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ParamExceptionAdvice {

    @ExceptionHandler(value = org.springframework.web.bind.MissingRequestCookieException.class)
    public Result missCookieParam(MissingRequestCookieException e) {
        return new Result(-400, "参数错误", null);
    }

    @ExceptionHandler(value = org.springframework.web.bind.MissingServletRequestParameterException.class)
    public Result missBodyParam(MissingServletRequestParameterException e) {
        return new Result(-400, "参数错误", null);
    }
}