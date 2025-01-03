package com.cm.cmoj.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashSet;

/**
 * author DingTao
 * Date 2024/8/12 10:46
 */
@Slf4j
@Aspect
@Component
public class MdcAspect implements ApplicationContextAware  {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
    @Pointcut("@annotation(MdcDot)")
    public void mdcDot() {
    }
    @Around("mdcDot()")
    public Object mdcDot(ProceedingJoinPoint joinPoint) throws Throwable {
        HashSet<Object> objects = new HashSet<>();

        log.info("检测到接口切面");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        signature.getMethod();
        return joinPoint.proceed();
    }
}
