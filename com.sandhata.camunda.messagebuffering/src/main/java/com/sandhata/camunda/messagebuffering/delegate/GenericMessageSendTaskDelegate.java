package com.sandhata.camunda.messagebuffering.delegate;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandhata.camunda.messagebuffering.apicall.ApiCall;
import com.sandhata.camunda.messagebuffering.constants.MessageBufferingConstants;
import com.sandhata.camunda.messagebuffering.exception.MessageBufferingCustomException;
import com.sandhata.camunda.messagebuffering.exception.MessageBufferingExceptions;
import com.sandhata.camunda.messagebuffering.exception.RetryErrorException;
import com.sandhata.camunda.messagebuffering.models.ErrorResponse;
import com.sandhata.camunda.messagebuffering.models.MessageBufferData;
import com.sandhata.camunda.messagebuffering.models.SuccessResponse;

import lombok.extern.slf4j.Slf4j;

@Component(value = "GenericMessageSendTask")
@Slf4j
public class GenericMessageSendTaskDelegate implements JavaDelegate {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ApiCall apicall;

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		String transactionId = objectMapper
				.convertValue(execution.getVariable(MessageBufferingConstants.TRANSACTION_ID), String.class);

		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.GENERIC_MESSAGE_SEND_TASK_DELEGATE, MessageBufferingConstants.EXECUTE,
				MessageBufferingConstants.START);

		MessageBufferData messageBufferData = objectMapper.convertValue(
				execution.getVariable(MessageBufferingConstants.PROCESS_VARIABLES), MessageBufferData.class);
		
		SuccessResponse response = null;
		
		try {
			
			// Invoke API call to get execution Id
			String executionId = apicall.getExecutionId(messageBufferData, transactionId);

			// Invoke Api call to trigger message API call with the execution id fetched
			response = apicall.triggerMessageCatchEvent(messageBufferData, transactionId, executionId);

		} catch (RetryErrorException exc) {
			
			execution.setVariable(MessageBufferingConstants.RETRY_ERROR, MessageBufferingConstants.TRUE);
			execution.setVariable(MessageBufferingConstants.ERROR_OBJECT, MessageBufferingConstants.RETRY_EXCEPTION);
			execution.setVariable(MessageBufferingConstants.ERROR_VALUE, exc);
			
			log.error(MessageBufferingConstants.OUT, transactionId,
					MessageBufferingConstants.GENERIC_MESSAGE_SEND_TASK_DELEGATE, MessageBufferingConstants.EXECUTE,
					MessageBufferingConstants.FAILED, MessageBufferingConstants.DELEGATE,
					objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exc));

			throw new BpmnError("" + HttpStatus.BAD_REQUEST.value(), exc);
			
		} catch (MessageBufferingCustomException exc) {
			
			execution.setVariable(MessageBufferingConstants.RETRY_ERROR, MessageBufferingConstants.FALSE);
			execution.setVariable(MessageBufferingConstants.ERROR_OBJECT, MessageBufferingConstants.MESSAGE_BUFFERING_CUSTOM_EXCEPTION);
			execution.setVariable(MessageBufferingConstants.ERROR_VALUE, exc);
			
			log.error(MessageBufferingConstants.OUT, transactionId,
					MessageBufferingConstants.GENERIC_MESSAGE_SEND_TASK_DELEGATE, MessageBufferingConstants.EXECUTE,
					MessageBufferingConstants.FAILED, MessageBufferingConstants.DELEGATE,
					objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exc));

			throw new BpmnError("" + HttpStatus.BAD_REQUEST.value(), exc);
		}catch (Exception exc) {
			
			ErrorResponse errorResposne = new ErrorResponse(
					MessageBufferingExceptions.MISMATCHING_MESSAGE_CORRELATION_EXCEPTION,
					MessageBufferingConstants.GENERIC_MESSAGE_SEND_TASK_DELEGATE, MessageBufferingConstants.EXECUTE, transactionId);
			
			MessageBufferingCustomException messageBufferingCustomException = new MessageBufferingCustomException(
					errorResposne);
			
			execution.setVariable(MessageBufferingConstants.RETRY_ERROR, MessageBufferingConstants.FALSE);
			execution.setVariable(MessageBufferingConstants.ERROR_OBJECT, MessageBufferingConstants.MESSAGE_BUFFERING_CUSTOM_EXCEPTION);
			execution.setVariable(MessageBufferingConstants.ERROR_VALUE, messageBufferingCustomException);
			
			log.error(MessageBufferingConstants.OUT, transactionId,
					MessageBufferingConstants.GENERIC_MESSAGE_SEND_TASK_DELEGATE, MessageBufferingConstants.EXECUTE,
					MessageBufferingConstants.FAILED, MessageBufferingConstants.DELEGATE,
					objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(messageBufferingCustomException));

			throw new BpmnError("" + HttpStatus.INTERNAL_SERVER_ERROR.value(), messageBufferingCustomException);
		}

		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.GENERIC_MESSAGE_SEND_TASK_DELEGATE, MessageBufferingConstants.EXECUTE,
				MessageBufferingConstants.END);

	}

}
