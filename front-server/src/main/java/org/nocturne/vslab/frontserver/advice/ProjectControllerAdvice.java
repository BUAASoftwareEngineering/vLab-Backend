package org.nocturne.vslab.frontserver.advice;

import org.nocturne.vslab.frontserver.bean.Result;
import org.nocturne.vslab.frontserver.exceptiion.project.ProjectCreateLimitException;
import org.nocturne.vslab.frontserver.exceptiion.project.ProjectNotFoundException;
import org.nocturne.vslab.frontserver.exceptiion.project.UnknownProjectTypeException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectControllerAdvice {

    @ExceptionHandler(value = ProjectNotFoundException.class)
    public Result projectNotFoundHandler(ProjectNotFoundException e) {
        return new Result(ProjectNotFoundException.CODE, "项目拒绝访问", null);
    }

    @ExceptionHandler(value = UnknownProjectTypeException.class)
    public Result unknownProjectTypeHandler(UnknownProjectTypeException e) {
        return new Result(UnknownProjectTypeException.CODE, "项目类型不存在", null);
    }

    @ExceptionHandler(value = ProjectCreateLimitException.class)
    public Result projectCreateLimitHandler(ProjectCreateLimitException e) {
        return new Result(ProjectCreateLimitException.CODE, "该类项目创建达到上线", null);
    }
}
