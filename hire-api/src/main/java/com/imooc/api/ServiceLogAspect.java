package com.imooc.api;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Slf4j
@Aspect
public class ServiceLogAspect {

    @Around("execution(* com.imooc.service.impl..*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start("task1");

        long begin = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();
        String point = joinPoint.getTarget().getClass().getName()
                + "."
                + joinPoint.getSignature().getName();

        long end = System.currentTimeMillis();

//        stopWatch.stop();
//        log.info(stopWatch.prettyPrint());
//        log.info(stopWatch.shortSummary());
//        System.out.println(stopWatch.getTotalTimeMillis());
//        System.out.println(stopWatch.getTaskCount());

        long takeTime = end - begin;
        if (takeTime > 500) {
            log.error("执行方法：{} 执行时间太长，耗费{}毫秒", point, takeTime);
        } else if (takeTime > 300) {
            log.warn("执行方法：{} 执行时间较长，耗费{}毫秒", point, takeTime);
        } else {
            log.info("执行方法：{} 执行时间，耗费{}毫秒", point, takeTime);
        }

        return proceed;
    }
}
