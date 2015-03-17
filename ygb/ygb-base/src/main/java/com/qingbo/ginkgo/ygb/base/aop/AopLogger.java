package com.qingbo.ginkgo.ygb.base.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.qingbo.ginkgo.ygb.common.result.Result;

/**
 * 使用AOP记日志并捕获异常
 * @author hongwei
 * @date 2014-11-12
 */
@Aspect
@Component
public class AopLogger {
	private String ginkgoPackage = "com.qingbo.ginkgo.ygb";
	private String aopLoggerClassName = getClass().getName();
	
	@Around(value="execution(* com.qingbo.ginkgo.ygb.*.service.*.*(..))")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		String targetName = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Logger logger = LoggerFactory.getLogger(targetName);
        
        Logger callerLogger = null;
        StringBuilder caller = new StringBuilder();
        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        if (stackTraceElements != null) {
        	for(int i=0;i<stackTraceElements.length;i++) {
        	StackTraceElement stackTraceElement = stackTraceElements[i];
        	String className = stackTraceElement.getClassName();
        	int lastDot = className.lastIndexOf('.');
        	String packageName = className.substring(0, lastDot);
        	if(!packageName.startsWith(ginkgoPackage) || className.equals(aopLoggerClassName) || className.contains("$")) continue;
        	
        	String simpleClassName = className.substring(lastDot+1);
        	caller.append(' ');
        	caller.append(simpleClassName);
        	caller.append('.');
        	caller.append(stackTraceElement.getMethodName());
        	caller.append(':');
        	caller.append(stackTraceElement.getLineNumber());
        	
        	callerLogger = LoggerFactory.getLogger(stackTraceElement.getClassName());
        	break;
        	}
        }
        
        String requestInfo = methodName+Arrays.toString(arguments)+caller.toString();
        String callerRequestInfo = targetName.substring(targetName.lastIndexOf('.')+1)+"."+requestInfo;
        try {
			Object proceed = joinPoint.proceed();
			if(proceed instanceof Result) {
				Result<?> result = (Result<?>)proceed;
				String responseInfo = proceed.toString();
				if(result.success()) {
					logger.info(requestInfo);
					logger.info(responseInfo);
					if(callerLogger!=null) {
						callerLogger.info(callerRequestInfo);
						callerLogger.info(responseInfo);
					}
				}else {
					logger.warn(requestInfo);
					logger.warn(responseInfo);
					if(callerLogger!=null) {
						callerLogger.warn(callerRequestInfo);
						callerLogger.warn(responseInfo);
					}
				}
			}else {
				String responseInfo = proceed!=null ? proceed.toString() : "Result is null";
				logger.info(requestInfo);
				logger.info(responseInfo);
				if(callerLogger!=null) {
					callerLogger.info(callerRequestInfo);
					callerLogger.info(responseInfo);
				}
			}
			return proceed;
        }catch (Exception e) {
			Result<Object> result = Result.newException(e);
			String responseInfo = result.toString();
			result.setMessage(e.getMessage());
			logger.warn(requestInfo);
			logger.warn(responseInfo);
			if(callerLogger!=null) {
				callerLogger.warn(callerRequestInfo);
				callerLogger.warn(responseInfo);
			}
			return result;
		}
	}
}
