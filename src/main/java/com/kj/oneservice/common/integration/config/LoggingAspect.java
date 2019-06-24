package com.kj.oneservice.common.integration.config;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

	
	@Pointcut("execution(* com.kj.*.*.*.*.*(..)) || execution(* com.kj.*.*.*.*.*.*(..)) ")
	protected void methodEntryExitLogging() {
		
	}
	
	@Pointcut("execution(* com.kj.*.*.integration.dao.*.*(..))")
	protected void timeTakenLogging() {
		
	}
	
	@Before("methodEntryExitLogging()")
	@Order(1)
	public void logEntry(JoinPoint joinpoint) {
		final Log logger = getLogger(joinpoint);
		final String logPrefix = getLogPrefix(joinpoint);
		
		logger.debug(logPrefix + "(" + Arrays.deepToString(joinpoint.getArgs()) + ") :: start");
	}
	
	@AfterReturning(pointcut = "methodEntryExitLogging()", returning = "result")
	@Order(2)
	public void logExit(JoinPoint joinpoint, Object result) {
		final Log logger = getLogger(joinpoint);
		final String logPrefix = getLogPrefix(joinpoint);
		
		logger.debug(logPrefix + " :: End. Return value :: [ " + result + " ]");
	}
	
	@AfterThrowing(pointcut = "methodEntryExitLogging()", throwing = "throwable")
	@Order(3)
	public void logAfterThrowing(JoinPoint joinpoint, Throwable throwable) {
		final Log logger = getLogger(joinpoint);
		final String logPrefix = getLogPrefix(joinpoint);
		
		logger.debug(logPrefix + " :: End. Return valueAn Exception has been thrown :: cause - " + throwable.getCause() );
	}
	
	@Around("timeTakenLogging()")
	@Order(4)
	public void logTimeTaken(ProceedingJoinPoint joinpoint) throws Throwable{
		final Log logger = getLogger(joinpoint);
		final String logPrefix = getLogPrefix(joinpoint);
		
		long start = System.nanoTime();
		/*Object result = */joinpoint.proceed();
		long end = System.nanoTime();
		logger.debug(logPrefix + " :: Time taken " + ((double) (end-start) / 1000000) + " ms");
	}
	
	
	
	private static Log getLogger(JoinPoint joinpoint) {
		return LogFactory.getLog(joinpoint.getTarget().getClass());
	}
	
	private static String getLogPrefix(JoinPoint joinpoint) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(joinpoint.getSignature().getName());
		return builder.toString();
	}
}
