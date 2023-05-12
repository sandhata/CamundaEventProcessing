package com.sandhata.camunda.messagebuffering.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandhata.camunda.messagebuffering.constants.MessageBufferingConstants;
import com.sandhata.camunda.messagebuffering.models.MessageBufferData;

import lombok.extern.slf4j.Slf4j;

@Component(value = "ReadErrorMessage")
@Slf4j
public class ReadErrorMessageDelegate implements JavaDelegate {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		String transactionId = objectMapper
				.convertValue(execution.getVariable(MessageBufferingConstants.TRANSACTION_ID), String.class);

		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.READ_ERROR_MESSAGE_DELEGATE, MessageBufferingConstants.EXECUTE,
				MessageBufferingConstants.START);

		Boolean retryError = objectMapper.convertValue(execution.getVariable(MessageBufferingConstants.RETRY_ERROR),
				Boolean.class);

		Integer retryCount = objectMapper.convertValue(execution.getVariable(MessageBufferingConstants.RETRY_COUNT),
				Integer.class);

		Integer retryDelay = objectMapper.convertValue(execution.getVariable(MessageBufferingConstants.RETRY_DELAY),
				Integer.class);

		String retryCountString = objectMapper
				.convertValue(execution.getVariable(MessageBufferingConstants.RETRY_COUNT_STRING), String.class);

		String retryDelayString = objectMapper
				.convertValue(execution.getVariable(MessageBufferingConstants.RETRY_DELAY_STRING), String.class);

		MessageBufferData messageBufferData = null;

		if (execution.hasVariable(MessageBufferingConstants.PROCESS_VARIABLES)) {
			messageBufferData = objectMapper.convertValue(
					execution.getVariable(MessageBufferingConstants.PROCESS_VARIABLES), MessageBufferData.class);
		}

		log.info("Retry Count String : {}", retryCountString);
		log.info("Retry Delay String : {}", retryDelayString);
		log.info("Retry Error : {}", retryError);
		log.info("Retry Count : {}", retryCount);
		log.info("Retry Delay : {}", retryDelay);
		log.info("Process Variables : {}",
				objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(messageBufferData));

		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.READ_ERROR_MESSAGE_DELEGATE, MessageBufferingConstants.EXECUTE,
				MessageBufferingConstants.END);

	}

}
