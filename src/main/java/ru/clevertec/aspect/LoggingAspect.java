package ru.clevertec.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class LoggingAspect {

    @Before("execution(* ru.clevertec.service..*(..))")
    public void logMethodEntry(JoinPoint joinPoint) {
        System.out.println("Entering method: " + joinPoint.getSignature().toShortString());
        for (Object arg : joinPoint.getArgs()) {
            System.out.println("  Arg: " + arg);
        }
    }

    @AfterReturning(pointcut = "execution(* ru.clevertec.service..*(..))", returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        System.out.println("Exiting method: " + joinPoint.getSignature().toShortString());
        System.out.println("  Result: " + result);
    }
}

