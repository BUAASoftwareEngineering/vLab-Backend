package org.nocturne.vslab.frontserver.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.nocturne.vslab.api.entity.Container;
import org.nocturne.vslab.frontserver.bean.Result;
import org.nocturne.vslab.frontserver.exceptiion.project.ProjectNotFoundException;
import org.nocturne.vslab.frontserver.mapper.ContainerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Aspect
@Order(2)
@Component
public class ProjectAuthorizeAspect {

    private ContainerMapper containerMapper;

    @Autowired
    public ProjectAuthorizeAspect(ContainerMapper containerMapper) {
        this.containerMapper = containerMapper;
    }

    @Pointcut("execution(public * org.nocturne.vslab.frontserver.controller.ProjectController.enterProject(..)) ||" +
            "execution(public * org.nocturne.vslab.frontserver.controller.ProjectController.exitProject(..)) ||" +
            "execution(public * org.nocturne.vslab.frontserver.controller.ProjectController.destroyProject(..)) ||" +
            "execution(public * org.nocturne.vslab.frontserver.controller.ProjectController.updateProjectInfo(..))")
    public void projectControllerMethods() {}

    @Pointcut("execution(public * org.nocturne.vslab.frontserver.controller.DirController.*(..)) ||" +
            "execution(public * org.nocturne.vslab.frontserver.controller.FileController.*(..))")
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

        try {
            Container container = containerMapper.getContainerById(projectId);
            if (!container.getUserId().equals(userId)) {
                throw new ProjectNotFoundException();
            }
        } catch (Exception e) {
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
