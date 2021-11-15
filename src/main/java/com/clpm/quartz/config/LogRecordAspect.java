package com.clpm.quartz.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class LogRecordAspect {


    @Pointcut("execution(* com.clpm.quartz.controller..*(..))")
    public void excudeService() {
    }


    @Around(value = "excudeService()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {

        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        request.setCharacterEncoding("UTF-8");

        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        String requestHeader = request.getHeader("Authorization");

        log.info("请求头中的携带参数{}",requestHeader);
        log.info("请求的方法为{},请求的url地址为{},请求的queryString{}",method,requestURI,queryString);

        return pjp.proceed();
    }

}
