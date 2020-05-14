package org.nocturne.vslab.backend.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.nocturne.vslab.backend.util.ContainerLiveKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(3)
@Component
public class ActiveContainerAspect {

    private final ContainerLiveKeeper keeper;

    @Autowired
    public ActiveContainerAspect(ContainerLiveKeeper keeper) {
        this.keeper = keeper;
    }

    @Pointcut("execution(public * org.nocturne.vslab.backend.controller.DirController.*(..)) ||" +
            "execution(public * org.nocturne.vslab.backend.controller.FileController.*(..))")
    public void communicateWithContainerMethods() {}

    @Around("communicateWithContainerMethods()")
    public String activeContainerKeeper(ProceedingJoinPoint joinPoint) throws Throwable {
        Integer projectId = (Integer) joinPoint.getArgs()[0];
        keeper.refreshActiveTime(projectId);

        return joinPoint.proceed().toString();
    }
}
