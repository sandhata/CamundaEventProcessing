package com.sandhata.camunda.messagebuffering.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "props")
@Data
@Component
public class ApplicationConfig {

	private String camundarestapibasepath;
	private String triggermessageurlpath;
	private String executionidurlpath;
	private Integer internalservicetimeout;
	private String isproxyenabled;
	private String httpproxyhost;
	private String httpproxyport;
	
}
