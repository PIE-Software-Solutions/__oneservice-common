package com.kj.oneservice.common.integration.swagger;


import java.lang.reflect.Method;
import java.util.Optional;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import com.kj.oneservice.common.integration.util.AppLogger;

import springfox.documentation.spring.web.PropertySourcedMapping;

public class ExtendedSwaggerProxyRequestHandlerMapping extends SwaggerProxyRequestMappingHandlerMapping{

	private static final AppLogger LOGGER = new AppLogger(ExtendedSwaggerProxyRequestHandlerMapping.class.getName());
	
	private final Environment environment;
	
	public ExtendedSwaggerProxyRequestHandlerMapping(Environment environment, Object handlerObject,
			String servicePath) {
		super(handlerObject, servicePath);
		this.environment = environment;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void doMapping(Class<?> clazz, Method method, RequestMappingInfo requestMappingInfo,
			HandlerMethod handlerMethod) {
		PropertySourcedMapping propertySourcedMapping = AnnotationUtils.getAnnotation(method, PropertySourcedMapping.class);
		String mappingPath = getMappingPath(propertySourcedMapping);
		
		
		if(mappingPath != null) {
			mappingPath = servicePath + mappingPath;
			LOGGER.catchInfo("URL path " + mappingPath + " with methid " + handlerMethod.toString());
		} else {
			super.doMapping(clazz, method, requestMappingInfo, handlerMethod);
		}
	}
	
	private String getMappingPath(final PropertySourcedMapping propertySourcedMapping) {
		String mappingPath = null;
		
		if(null != propertySourcedMapping) {
			final String propertyKey = propertySourcedMapping.propertyKey();
			mappingPath = (String) Optional.ofNullable(environment.getProperty(propertyKey))
					.map(param -> param.replace("${" + propertyKey + "}", param) ).orElse(null);
		}
		return mappingPath;
	}

}
