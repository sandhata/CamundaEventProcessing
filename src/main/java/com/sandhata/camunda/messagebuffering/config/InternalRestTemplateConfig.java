package com.sandhata.camunda.messagebuffering.config;

import java.time.Duration;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@lombok.Generated
@Configuration
@ComponentScan(basePackages = "com.sandhata.camunda.messagebuffer")
public class InternalRestTemplateConfig {

	@Autowired
	public ApplicationConfig applicationConfig;

	@Bean
	@Primary
	public RestTemplate restTemplate(RestTemplateBuilder builder) {

		Integer timeout = applicationConfig.getInternalservicetimeout().intValue();

		CloseableHttpClient client = getCloseableHttpClient();
		return builder.requestFactory(() -> new HttpComponentsClientHttpRequestFactory(client))
				.setConnectTimeout(Duration.ofMillis(timeout)).build();
	}

	private CloseableHttpClient getCloseableHttpClient() {

		CloseableHttpClient client;

		String isproxyenabled = applicationConfig.getIsproxyenabled();
		if (isproxyenabled.equalsIgnoreCase("true")) {
			String proxyHost = applicationConfig.getHttpproxyhost();
			int proxyPort = Integer.parseInt(applicationConfig.getHttpproxyport());
			RequestConfig requestConfig = RequestConfig.custom().setProxy(new HttpHost(proxyHost, proxyPort)).build();

			client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
		} else {
			int backendTimeout = applicationConfig.getInternalservicetimeout().intValue();
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(backendTimeout)
					.setConnectionRequestTimeout(backendTimeout).setSocketTimeout(backendTimeout).build();
			client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
		}
		return client;
	}

}
