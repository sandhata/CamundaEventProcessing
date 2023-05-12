package com.sandhata.camunda.messagebuffering.util;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MessageBufferingUtil {
	
	public String getRetryDelayInSec(Integer retryDelay) {
		String retryDelayString="PT"+retryDelay.intValue()+"S";
		log.info("Retry Delay in Seconds :: Generated Value : {}", retryDelayString);
		return retryDelayString;
	}
	
	public String getRetryDelayInMin(Integer retryDelay) {
		String retryDelayString ="PT"+retryDelay.intValue()+"M";
		log.info("Retry Delay in Minutes :: Generated Value : {}", retryDelayString);
		return retryDelayString;
	}
	
	public String getRetryCount(Integer retryCount) {
		String retryCountString="R"+retryCount.intValue();
		log.info("Retry Count :: Generated Value : {}", retryCountString);
		return retryCountString;
	}

}
