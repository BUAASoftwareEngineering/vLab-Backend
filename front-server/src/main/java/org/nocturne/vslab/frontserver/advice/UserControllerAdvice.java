package org.nocturne.vslab.frontserver.advice;

import org.nocturne.vslab.frontserver.bean.Result;
import org.nocturne.vslab.frontserver.exceptiion.project.UnknownProjectTypeException;
import org.nocturne.vslab.frontserver.exceptiion.user.UserAuthFailException;
import org.nocturne.vslab.frontserver.exceptiion.user.UserNotFoundException;
import org.nocturne.vslab.frontserver.exceptiion.user.UsernameAlreadyExist;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class UserControllerAdvice {


    @ExceptionHandler(value = UserNotFoundException.class)
    @ResponseBody
    public Result userNotFoundHandler(UserNotFoundException e) {
        return new Result(UserNotFoundException.CODE, "用户不存在", null);
    }

    @ExceptionHandler(value = UsernameAlreadyExist.class)
    @ResponseBody
    public Result usernameAlreadyExistHandler(UsernameAlreadyExist e) {
        return new Result(UsernameAlreadyExist.CODE, "用户名已存在", null);
    }

    @ExceptionHandler(value = UserAuthFailException.class)
    @ResponseBody
    public Result userAuthFailHandler(UserAuthFailException e) {
        return new Result(UserAuthFailException.CODE, "用户未登录", null);
    }

}
