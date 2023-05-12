package com.sandhata.camunda.messagebuffering.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandhata.camunda.messagebuffering.constants.MessageBufferingConstants;
import com.sandhata.camunda.messagebuffering.exception.MessageBufferingCustomException;
import com.sandhata.camunda.messagebuffering.exception.RetryErrorException;

import lombok.extern.slf4j.Slf4j;

@Component(value = "CreateCustomError")
@Slf4j
public class CreateCustomErrorDelegate implements JavaDelegate {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		String transactionId = objectMapper
				.convertValue(execution.getVariable(MessageBufferingConstants.TRANSACTION_ID), String.class);

		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.CREATE_CUSTOM_ERROR_DELEGATE, MessageBufferingConstants.EXECUTE,
				MessageBufferingConstants.START);

		String errorObject = objectMapper.convertValue(execution.getVariable(MessageBufferingConstants.ERROR_OBJECT),
				String.class);

		switch (errorObject) {
		case MessageBufferingConstants.MESSAGE_BUFFERING_CUSTOM_EXCEPTION:
			MessageBufferingCustomException messageBufferingCustomException = objectMapper.convertValue(
					execution.getVariable(MessageBufferingConstants.ERROR_VALUE),
					MessageBufferingCustomException.class);
			throw messageBufferingCustomException;
		case MessageBufferingConstants.RETRY_EXCEPTION:
			RetryErrorException retryErrorException = objectMapper.convertValue(
					execution.getVariable(MessageBufferingConstants.ERROR_VALUE), RetryErrorException.class);
			throw retryErrorException;
		default:
			log.info("Other Exception \n{}",
					objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorObject));
		}

		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.CREATE_CUSTOM_ERROR_DELEGATE, MessageBufferingConstants.EXECUTE,
				MessageBufferingConstants.END);

	}

}
