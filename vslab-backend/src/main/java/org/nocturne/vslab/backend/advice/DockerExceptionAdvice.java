package org.nocturne.vslab.backend.advice;

import com.github.dockerjava.api.exception.NotModifiedException;
import org.nocturne.vslab.backend.bean.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DockerExceptionAdvice {

    @ExceptionHandler(value = NotModifiedException.class)
    public Result containerStateNotModifiedHandler(NotModifiedException e) {
        return new Result(107, "容器已为目标状态", null);
    }
}
