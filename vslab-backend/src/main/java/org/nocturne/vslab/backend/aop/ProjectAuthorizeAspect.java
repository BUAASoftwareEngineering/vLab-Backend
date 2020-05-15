package org.nocturne.vslab.backend.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.nocturne.vslab.backend.bean.Project;
import org.nocturne.vslab.backend.bean.Result;
import org.nocturne.vslab.backend.exceptiion.project.ProjectNotFoundException;
import org.nocturne.vslab.backend.mapper.ProjectMapper;
import org.nocturne.vslab.backend.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@Aspect
@Order(2)
@Component
public class ProjectAuthorizeAspect {

    private final ProjectService projectService;

    @Autowired
    public ProjectAuthorizeAspect(ProjectMapper projectMapper,
                                  ProjectService projectService) {
        this.projectService = projectService;
    }

    @Pointcut("execution(public * org.nocturne.vslab.backend.controller.ProjectController.enterProject(..)) ||" +
            "execution(public * org.nocturne.vslab.backend.controller.ProjectController.exitProject(..)) ||" +
            "execution(public * org.nocturne.vslab.backend.controller.ProjectController.destroyProject(..)) ||" +
            "execution(public * org.nocturne.vslab.backend.controller.ProjectController.updateProjectInfo(..))")
    public void projectControllerMethods() {}

    @Pointcut("execution(public * org.nocturne.vslab.backend.controller.DirController.*(..)) ||" +
            "execution(public * org.nocturne.vslab.backend.controller.FileController.*(..))")
    public void fileAndDirControllerMethods() {}

    @Around("projectControllerMethods()")
    public Result checkProjectAuthorityForProject(ProceedingJoinPoint joinPoint) throws Throwable {
        doCheck(joinPoint);
        return (Result) joinPoint.proceed();
    }

    @Around("fileAndDirControllerMethods()")
    public String checkProjectAuthorityForFile(ProceedingJoinPoint joinPoint) throws Throwable {
        doCheck(joinPoint);
        return joinPoint.proceed().toString();
    }

    private void doCheck(ProceedingJoinPoint joinPoint) {
        Integer projectId = (Integer) joinPoint.getArgs()[0];
        Integer userId = getRequestUserId();

        List<Project> projects = projectService.getProjectsOfUser(userId);
        if (projects.stream().map(Project::getProjectId).noneMatch(x -> x.equals(projectId))) {
            throw new ProjectNotFoundException();
        }
    }

    private Integer getRequestUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes());
        HttpServletRequest request = attributes.getRequest();

        return Integer.parseInt(getCookieValue(request.getCookies(), "user_id"));
    }

    private String getCookieValue(Cookie[] cookies, String name) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }

        return "";
    }
}
