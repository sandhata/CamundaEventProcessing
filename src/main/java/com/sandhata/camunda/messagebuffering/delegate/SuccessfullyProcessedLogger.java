package com.sandhata.camunda.messagebuffering.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component(value = "SuccessfullyProcessed")
@Slf4j
class SuccessfullyProcessedLogger implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		log.info("Data is successfully processed");

	}

}
