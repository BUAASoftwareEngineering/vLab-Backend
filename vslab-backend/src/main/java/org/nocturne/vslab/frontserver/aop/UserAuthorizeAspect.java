package org.nocturne.vslab.frontserver.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.nocturne.vslab.frontserver.exceptiion.user.UserAuthFailException;
import org.nocturne.vslab.frontserver.util.UserTokenPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Aspect
@Order(1)
@Component
public class UserAuthorizeAspect {

    private UserTokenPool userTokenPool;

    @Autowired
    public UserAuthorizeAspect(UserTokenPool userTokenPool) {
        this.userTokenPool = userTokenPool;
    }

    @Pointcut("execution(public * org.nocturne.vslab.frontserver.controller.*.*(..)) && " +
            "!execution(public * org.nocturne.vslab.frontserver.controller.UserController.login(..)) && " +
            "!execution(public * org.nocturne.vslab.frontserver.controller.UserController.logout(..)) && " +
            "!execution(public * org.nocturne.vslab.frontserver.controller.UserController.register(..))")
    public void authPointcut(){}

    @Before("authPointcut()")
    public void authorizeLoginState() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes());
            HttpServletRequest request = attributes.getRequest();

            Cookie[] cookies = request.getCookies();
            Integer userId = Integer.parseInt(Objects.requireNonNull(getCookieValue(cookies, "user_id")));
            String userToken = getCookieValue(cookies, "user_token");

            if (!userTokenPool.isUserTokenAccepted(userId, userToken)) {
                throw new UserAuthFailException();
            }
        } catch (Exception e) {
            throw new UserAuthFailException();
        }
    }

    private String getCookieValue(Cookie[] cookies, String name) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
