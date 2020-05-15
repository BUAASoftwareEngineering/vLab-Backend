package org.nocturne.vslab.backend.advice;

import org.nocturne.vslab.backend.bean.Result;
import org.nocturne.vslab.backend.exceptiion.user.UserAuthFailException;
import org.nocturne.vslab.backend.exceptiion.user.UserNotFoundException;
import org.nocturne.vslab.backend.exceptiion.user.UserAlreadyExist;
import org.nocturne.vslab.backend.exceptiion.user.WrongCaptchaException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserControllerAdvice {


    @ExceptionHandler(value = UserNotFoundException.class)
    public Result userNotFoundHandler(UserNotFoundException e) {
        return new Result(UserNotFoundException.CODE, "用户不存在", null);
    }

    @ExceptionHandler(value = UserAlreadyExist.class)
    public Result usernameAlreadyExistHandler(UserAlreadyExist e) {
        return new Result(UserAlreadyExist.CODE, "用户名或邮箱已存在", null);
    }

    @ExceptionHandler(value = UserAuthFailException.class)
    public Result userAuthFailHandler(UserAuthFailException e) {
        return new Result(UserAuthFailException.CODE, "用户未登录", null);
    }

    @ExceptionHandler(value = WrongCaptchaException.class)
    public Result wrongCaptchaHandler(WrongCaptchaException e) {
        return new Result(WrongCaptchaException.CODE, "验证码错误", null);
    }
}
