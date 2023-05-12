package com.sandhata.camunda.messagebuffering.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandhata.camunda.messagebuffering.constants.MessageBufferingConstants;
import com.sandhata.camunda.messagebuffering.models.MessageBufferData;

import lombok.extern.slf4j.Slf4j;

@Component(value = "MessageRetryExceeded")
@Slf4j
public class MessageRetryExceededDelegate implements JavaDelegate {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		String transactionId = objectMapper
				.convertValue(execution.getVariable(MessageBufferingConstants.TRANSACTION_ID), String.class);

		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.MESSAGE_RETRY_EXCEEDED_DELEGATE, MessageBufferingConstants.EXECUTE,
				MessageBufferingConstants.START);

		log.info("Maximum retry attempts exceeded");

		int retryCount = objectMapper
				.convertValue(execution.getVariable(MessageBufferingConstants.RETRY_COUNT), Integer.class).intValue();

		Integer retryDelay = objectMapper
				.convertValue(execution.getVariable(MessageBufferingConstants.RETRY_DELAY), Integer.class).intValue();

		MessageBufferData messageBufferData = objectMapper.convertValue(
				execution.getVariable(MessageBufferingConstants.PROCESS_VARIABLES), MessageBufferData.class);

		log.info("Retry Count : {} :: retry delay : {} :: Message Buffer Data \n{}", retryCount, retryDelay,
				objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(messageBufferData));

		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.MESSAGE_RETRY_EXCEEDED_DELEGATE, MessageBufferingConstants.EXECUTE,
				MessageBufferingConstants.END);

	}

}
