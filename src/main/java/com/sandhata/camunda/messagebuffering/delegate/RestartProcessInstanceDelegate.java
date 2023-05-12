package com.sandhata.camunda.messagebuffering.delegate;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandhata.camunda.messagebuffering.constants.MessageBufferingConstants;
import com.sandhata.camunda.messagebuffering.models.MessageBufferData;
import com.sandhata.camunda.messagebuffering.service.MessageBufferingService;

import lombok.extern.slf4j.Slf4j;

@Component(value = "RestartProcessInstance")
@Slf4j
public class RestartProcessInstanceDelegate implements JavaDelegate {

	@Autowired
	private MessageBufferingService messageBufferingService;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private RuntimeService runtimeService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		String transactionId = objectMapper
				.convertValue(execution.getVariable(MessageBufferingConstants.TRANSACTION_ID), String.class);

		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.RESTART_PROCESS_INSTANCE_DELEGATE, MessageBufferingConstants.EXECUTE,
				MessageBufferingConstants.START);

		int retryCount = objectMapper
				.convertValue(execution.getVariable(MessageBufferingConstants.RETRY_COUNT), Integer.class).intValue();
		
		Integer retryDelay = objectMapper
				.convertValue(execution.getVariable(MessageBufferingConstants.RETRY_DELAY), Integer.class).intValue();
		
		MessageBufferData messageBufferData = objectMapper
				.convertValue(execution.getVariable(MessageBufferingConstants.PROCESS_VARIABLES), MessageBufferData.class);

		log.info("API Call Invoked :: retry count : {}", retryCount);

		execution.setVariable(MessageBufferingConstants.RETRY_COUNT, --retryCount);

		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.RESTART_PROCESS_INSTANCE_DELEGATE, MessageBufferingConstants.EXECUTE,
				MessageBufferingConstants.END);
		
		//Should I restart the entire process or just the process instance ?
		messageBufferingService.messageBufferRetryLogicService(transactionId, retryCount, retryDelay, messageBufferData);

	}

}
