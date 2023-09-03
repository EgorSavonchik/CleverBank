package ru.clevertec.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

//@Slf4j
@Aspect
public class LoggingAspect {

    /*@Before("execution(* ru.clevertec.service..*(..)) || execution(* ru.clevertec.model..*(..))")
    public void logMethodEntry(JoinPoint joinPoint) {
        log.info("Entering method: {}", joinPoint.getSignature().toShortString());
        for (Object arg : joinPoint.getArgs()) {
            log.info("  Arg: " + arg);
        }
    }

    @AfterReturning(pointcut = "execution(* ru.clevertec.service..*(..))", returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        log.info("Exiting method: {}", joinPoint.getSignature().toShortString());
        log.info("  Result: " + result);
    }*/
}

