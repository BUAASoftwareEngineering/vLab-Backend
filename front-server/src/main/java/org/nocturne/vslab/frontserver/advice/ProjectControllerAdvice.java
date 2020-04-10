package org.nocturne.vslab.frontserver.advice;

import org.nocturne.vslab.frontserver.bean.Result;
import org.nocturne.vslab.frontserver.exceptiion.project.ProjectNotFoundException;
import org.nocturne.vslab.frontserver.exceptiion.project.UnknownProjectTypeException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ProjectControllerAdvice {

    @ExceptionHandler(value = ProjectNotFoundException.class)
    @ResponseBody
    public Result projectNotFoundHandler(ProjectNotFoundException e) {
        return new Result(ProjectNotFoundException.CODE, "项目不存在", null);
    }


    @ExceptionHandler(value = UnknownProjectTypeException.class)
    @ResponseBody
    public Result unknownProjectTypeHandler(UnknownProjectTypeException e) {
        return new Result(UnknownProjectTypeException.CODE, "项目类型不存在", null);
    }
}
