package com.kj.oneservice.common.integration.config;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import static com.kj.oneservice.common.integration.util.CommonConstants.SERVICE_NAME;
import static com.kj.oneservice.common.integration.util.CommonConstants.COMMON_PACKAGE_STRUCTURE;

import org.hibernate.annotations.ValueGenerationType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.kj.oneservice.common.integration.swagger.ExtendedSwaggerProxyRequestHandlerMapping;
import com.kj.oneservice.common.integration.swagger.SwaggerProxyRequestMappingHandlerMapping;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiResourceController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import springfox.documentation.swagger2.web.Swagger2Controller;

@Configuration
@PropertySource("file:${app.home}/${app.prop}.properties")
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurerAdapter{
	
	private static final String SWAGGER_BASE_PATH = "/" + SERVICE_NAME;
	
	@Bean
	public Docket swaggerApi(@Value("${swagger.host.port:swagger.host.port}") final String swaggerHostPort) {
		
		if(isNotBlank(swaggerHostPort)) {
			return new Docket(DocumentationType.SWAGGER_2).useDefaultResponseMessages(false).host(swaggerHostPort)
					.select().apis(RequestHandlerSelectors.basePackage(COMMON_PACKAGE_STRUCTURE))
					.paths(PathSelectors.any()).build();
		} else {
			return new Docket(DocumentationType.SWAGGER_2).useDefaultResponseMessages(false)
					.select().apis(RequestHandlerSelectors.basePackage(COMMON_PACKAGE_STRUCTURE))
					.paths(PathSelectors.any()).build();
		}
	}
	
	@Bean
	public HandlerMapping extendedSwaggerProxyRequestMappingHandlerMapping(Environment environment,
			DocumentationCache documentationCache, ServiceModelToSwagger2Mapper mapper, JsonSerializer jsonSerializer) {
		return new ExtendedSwaggerProxyRequestHandlerMapping(environment, 
				new Swagger2Controller(environment, documentationCache, mapper, jsonSerializer), SWAGGER_BASE_PATH);
	}
	
	@Bean
	public HandlerMapping swaggerProxyRequestMappingHandlerMapping(ApiResourceController apiResourceController) {
		return new SwaggerProxyRequestMappingHandlerMapping(apiResourceController, SWAGGER_BASE_PATH);
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
		resourceHandlerRegistry.addResourceHandler(SWAGGER_BASE_PATH + "/swagger.ui.html**")
								.addResourceLocations("classpath:/META-INF/resources/swagger.ui.html");
		resourceHandlerRegistry.addResourceHandler(SWAGGER_BASE_PATH + "/webjars/**")
								.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
}
