package com.kj.oneservice.common.integration.util;

import org.apache.log4j.Logger;

public class AppLogger {
	
	private Logger logger;
	
	public AppLogger(String className) {
		logger = Logger.getLogger(className);
		
	}
	
	@Deprecated
	public void startMethod(String methodName) {
		logger.debug(methodName + " ::: Start");
	}
	
	@Deprecated
	public void endMethod(String methodName) {
		logger.debug(methodName + " ::: End");
	}
	
	public void catchInfo(String methodName) {
		logger.info(methodName + " ::: Inside catch block");
	}
	
	public void error(String methodName, String message) {
		logger.error(methodName + " ::: " + message);
	}
	
	public void info(String... message) {
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for(String msg : message) {
			stringBuilder.append(msg);
		}
		logger.info(stringBuilder.toString());
	}
	
	public void debug(String methodName, String message) {
		logger.debug(methodName + " ::: " + message);
	}
	
	public void debug(String methodName, String... message) {
		
		if(logger.isDebugEnabled()) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(methodName).append(" :::: ");
			for(String msg : message) {
				stringBuilder.append(msg);
			}
			logger.info(methodName + " ::: " + stringBuilder.toString());
		}
	}
	
	public void error(Throwable throwable) {
		logger.error(throwable.getMessage());
		stackTraceToString(throwable);
	}

	private void stackTraceToString(Throwable throwable) {
		// TODO Auto-generated method stub
		
		logger.error(throwable.getMessage());
		StringBuilder stringBuilder = new StringBuilder();
		for(StackTraceElement traceElement : throwable.getStackTrace()) {
			stringBuilder.append(traceElement.toString()).append("\n");
		}
		logger.error(stringBuilder.toString());
		
	}
	
public void info(Object... message) {
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for(Object msg : message) {
			stringBuilder.append(msg);
		}
		logger.info(stringBuilder.toString());
	}

}
