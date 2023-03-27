package by.dudko.newsportal.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.StringJoiner;

@Aspect
@Slf4j
@Component
public class LoggingAspect {
    @Pointcut(value = "@within(org.springframework.stereotype.Service)")
    public void isService  () {
    }

    @Pointcut(value = "@within(org.springframework.web.bind.annotation.RestController)")
    public void isRestController() {
    }

    @Pointcut(value = "this(org.springframework.data.repository.Repository)")
    public void isRepository() {
    }

    @Around("isRestController() || isService() || isRepository()")
    public Object aroundLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodSignature = buildMethodSignature(signature);
        Object[] args = joinPoint.getArgs();
        String argsString = Arrays.toString(args);
        log.debug("Class [{}], methodSignature [{}]. Invoked with args {}", className, methodSignature, argsString);
        try {
            Object result = joinPoint.proceed(args);
            log.debug("Method [{}] returned value [{}]. Invoked with args {}", methodSignature, result, argsString);
            return result;
        } catch (Throwable ex) {
            log.warn("Exception has been thrown during method execution [{}]. Invoked with args {}. Error message [{}]",
                    methodSignature, argsString, ex.getMessage());
            throw ex;
        }
    }

    private String buildMethodSignature(MethodSignature methodSignature) {
        String[] parameterNames = methodSignature.getParameterNames();
        String methodName = methodSignature.getName();
        if (parameterNames.length == 0) {
            return "()";
        }
        StringJoiner argsDescription = new StringJoiner(", ", "(", ")");
        var parameterTypes = methodSignature.getParameterTypes();
        for (int i = 0; i < parameterNames.length; i++) {
            argsDescription.add(MessageFormat.format("{0} {1}",
                    parameterTypes[i].getSimpleName(), parameterNames[i]));
        }
        return methodName + argsDescription;
    }
}
