package com.kj.oneservice.common.integration.config;

import static com.kj.oneservice.common.integration.util.CommonConstants.ALLOWED_SERVICE_PATHS;
import static com.kj.oneservice.common.integration.util.CommonConstants.ONESERVICE_DATA_SOURCE;
import static com.kj.oneservice.common.integration.util.CommonConstants.SEC_REQ;
import static com.kj.oneservice.common.integration.util.CommonConstants.SERVICE_NAME;
import static com.kj.oneservice.common.integration.util.CommonConstants.YES;
import static com.kj.oneservice.common.integration.util.SQLQueryConstants.GET_USERS;
import static com.kj.oneservice.common.integration.util.SQLQueryConstants.GET_USER_ROLES;
import static org.apache.commons.lang3.StringUtils.isBlank;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class CommonSecurityConfig extends WebSecurityConfigurerAdapter{

	public static final String[] SPRING_ACTUATOR_PATHS = new String[] { "/health", "/metrics", "/monitoring",
					"/" + SERVICE_NAME + "/swagger-ui-html", "/" + SERVICE_NAME + "/webjars/**",
					"/" + SERVICE_NAME + "/swagger-resources/**", "/" + SERVICE_NAME + "/v2/api-docs" };
	
	@Autowired
	@Qualifier(ONESERVICE_DATA_SOURCE)
	private DataSource oneserviceDataSource;
	
	@Autowired
	public void configAuthentication(AuthenticationManagerBuilder authBuilder) throws Exception{
		
		if(!isBlank(SEC_REQ) && SEC_REQ.equalsIgnoreCase(YES)) {
			authBuilder.jdbcAuthentication().dataSource(oneserviceDataSource).passwordEncoder(passwordEncoder())
							.usersByUsernameQuery(GET_USERS).authoritiesByUsernameQuery(GET_USER_ROLES);
		}
	}
	
	@Override
	//@Order(SecurityProperties.BASIC_AUTH_ORDER)
	protected void configure(HttpSecurity httpSecurity)throws Exception{
		if(!isBlank(SEC_REQ) && SEC_REQ.equalsIgnoreCase(YES)) {
			httpSecurity.authorizeRequests().antMatchers(SPRING_ACTUATOR_PATHS).permitAll().and().authorizeRequests()
							.antMatchers(ALLOWED_SERVICE_PATHS).authenticated().anyRequest().access("hasRole('ROLE_USER')")
							.and().authorizeRequests().antMatchers("/shutdown").authenticated().anyRequest()
							.access("hasRole('ROLE_ADMIN')");
			httpSecurity.csrf().disable();
			httpSecurity.httpBasic();
		}
		else {
			httpSecurity.authorizeRequests().antMatchers(SPRING_ACTUATOR_PATHS).permitAll().and().authorizeRequests()
				.antMatchers(ALLOWED_SERVICE_PATHS).permitAll()
				.and().authorizeRequests().antMatchers("/shutdown").authenticated().anyRequest()
				.access("hasRole('ROLE_ADMIN')");
			httpSecurity.csrf().disable();
			httpSecurity.httpBasic();
		}
	}
	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
}
