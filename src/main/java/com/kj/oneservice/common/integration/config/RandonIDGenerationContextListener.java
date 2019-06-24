package com.kj.oneservice.common.integration.config;

import static com.kj.oneservice.common.integration.util.CommonConstants.REQUEST_PATTERN;

import java.util.UUID;

import javax.servlet.ServletRequestEvent;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

import com.kj.oneservice.common.integration.util.AppLogger;

@Configuration
@WebListener
public class RandonIDGenerationContextListener extends RequestContextListener{
	
	private static final AppLogger LOGGER = new AppLogger(RandonIDGenerationContextListener.class.getName());
	
	public void reuqestInitialized(ServletRequestEvent requestEvent) {
		MDC.put(REQUEST_PATTERN, UUID.randomUUID());
		LOGGER.debug("Request Initiated for :: " + MDC.get(REQUEST_PATTERN));
	}
	
	public void requestDestroyed(ServletRequestEvent requestEvent) {
		LOGGER.debug("Request Destroyed for :: " + MDC.get(REQUEST_PATTERN));
		MDC.clear();
	}

}
