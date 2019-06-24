package com.kj.oneservice.common.integration.config;


import static com.kj.oneservice.common.integration.util.CommonConstants.ERROR_HEAP_SIZE;
import static com.kj.oneservice.common.integration.util.CommonConstants.OK;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Configuration;

import com.kj.oneservice.common.integration.util.AppLogger;

@Configuration
public class MsHealthCheck implements HealthIndicator{
	
	public static final AppLogger LOGGER = new AppLogger(MsHealthCheck.class.getName());
	
	
	@Override
	public Health health() {
		int errorCode = check();
		if(errorCode != 0) {
			return Health.down().withDetail("Heap Memory Full", errorCode).build();
			
		}
		return Health.up().build();
	}
	
	public int check() {
		long heapSize = Runtime.getRuntime().totalMemory();
		
		long heapMaxSize = Runtime.getRuntime().maxMemory();
		
		long heapFreeSize = Runtime.getRuntime().freeMemory();
		
		
		LOGGER.debug("Heap Memory for TotalheapSize :: " + heapSize + " MaxheapSize :: " + heapMaxSize + " FreeheapSize :: " + heapFreeSize);
		
		if(heapFreeSize <= 0 || (( heapSize-heapFreeSize) > heapMaxSize))
		{
			return ERROR_HEAP_SIZE;
		}
		return OK;
	}

}
