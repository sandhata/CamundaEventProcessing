package com.sandhata.camunda.messagebuffering.service;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sandhata.camunda.messagebuffering.models.Employee;
import com.sandhata.camunda.messagebuffering.models.MessageBufferData;
import com.sandhata.camunda.messagebuffering.models.SuccessResponse;

public interface MessageBufferingService {

	public SuccessResponse validateMessageService(Employee employee, String transactionId, String processName,
			Integer retryCount, Integer retryDelay) throws JsonProcessingException;

	public SuccessResponse messageCorrelationEventService(String transactionId, Employee employeeMessage,
			String processName) throws JsonProcessingException;

	public SuccessResponse messageBufferRetryLogicService(String transactionId, Integer retryCount, Integer retryDelay,
			MessageBufferData messageBufferData) throws JsonProcessingException;

	public HttpStatus getStatusCode(String responseCode);
}
