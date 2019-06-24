package com.kj.oneservice.common.integration.config;

import static com.kj.oneservice.common.integration.util.CommonConstants.ONESERVICE_DATA_SOURCE;

import java.util.Arrays;
import java.util.Base64;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:${app.home}/${app.prop}.properties")
public class DatabaseConfig {

	
	@Bean(name = ONESERVICE_DATA_SOURCE)
	@Primary
	@Profile("readonly")
	@ConfigurationProperties(prefix = "readonly.datasource")
	public DataSource readonlyOneServiceDataSource(
						@Value("${readonly.datasource.passwd:readonly.datasource.passwd}") byte[] passwd) {
		DataSource dataSource = createDataSource(passwd);
		return dataSource;
	}
	
	@Bean(name = ONESERVICE_DATA_SOURCE)
	@Primary
	@Profile("readwrite")
	@ConfigurationProperties(prefix = "readwrite.datasource")
	public DataSource readwriteOneServiceDataSource(
						@Value("${readwrite.datasource.passwd:readwrite.datasource.passwd}") byte[] passwd) {
		DataSource dataSource = createDataSource(passwd);
		return dataSource;
	}
	
	@Bean(name = ONESERVICE_DATA_SOURCE)
	@Primary
	@Profile("master")
	@ConfigurationProperties(prefix = "master.datasource")
	public DataSource masterOneServiceDataSource(
						@Value("${master.datasource.passwd:master.datasource.passwd}") byte[] passwd) {
		DataSource dataSource = createDataSource(passwd);
		return dataSource;
	}
	
	public DataSource createDataSource(byte[] passwd) {
		Base64.Decoder decoder = Base64.getDecoder();
		String passwdStr = new String(decoder.decode(passwd));
		
		DataSource dataSource = DataSourceBuilder.create().password(passwdStr).build();
		
		passwdStr = null;
		Arrays.fill(passwd, (byte) 0);
		
		return dataSource;
	}
}
