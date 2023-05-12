package com.sandhata.camunda.messagebuffering.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandhata.camunda.messagebuffering.constants.MessageBufferingConstants;
import com.sandhata.camunda.messagebuffering.exception.MessageBufferingCustomException;
import com.sandhata.camunda.messagebuffering.exception.MessageBufferingExceptions;
import com.sandhata.camunda.messagebuffering.models.Employee;
import com.sandhata.camunda.messagebuffering.models.ErrorResponse;
import com.sandhata.camunda.messagebuffering.models.MessageBufferData;
import com.sandhata.camunda.messagebuffering.models.SuccessResponse;
import com.sandhata.camunda.messagebuffering.util.MessageBufferingUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MessageBufferingServiceImpl implements MessageBufferingService {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private MessageBufferingUtil messageBufferingUtil;

	@Override
	public SuccessResponse validateMessageService(Employee employee, String transactionId, String processName,
			Integer retryCount, Integer retryDelay) throws JsonProcessingException {

		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.MESSAGE_BUFFERING_SERVICE, MessageBufferingConstants.VALIDATE_MESSAGE_SERVICE,
				MessageBufferingConstants.START);
		SuccessResponse successResponse = null;
		switch (processName) {
		case MessageBufferingConstants.MESSAGE_THROW_CATCH_PROCESS:
			log.info(MessageBufferingConstants.PROCESS_NAME_LOG, MessageBufferingConstants.MESSAGE_THROW_CATCH_PROCESS);

			startProcess(processName, employee.getEmployeeId().toString(), employee, transactionId, retryCount,
					retryDelay);

			successResponse = new SuccessResponse();
			successResponse.setResponseCode("" + HttpStatus.OK.value());
			successResponse.setMessage(HttpStatus.OK.getReasonPhrase());
			break;
		default:
			log.info(MessageBufferingConstants.PROCESS_NAME_LOG, MessageBufferingConstants.DEFAULT);
		}
		log.info(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.MESSAGE_BUFFERING_SERVICE,
				MessageBufferingConstants.VALIDATE_MESSAGE_SERVICE, MessageBufferingConstants.SUCCESS,
				MessageBufferingConstants.SERVICE,
				objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(successResponse));
		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.MESSAGE_BUFFERING_SERVICE, MessageBufferingConstants.VALIDATE_MESSAGE_SERVICE,
				MessageBufferingConstants.END);
		return successResponse;
	}

	@Override
	public SuccessResponse messageCorrelationEventService(String transactionId, Employee messageEmployee,
			String processName) throws JsonProcessingException {

		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.MESSAGE_BUFFERING_SERVICE,
				MessageBufferingConstants.MESSAGE_CORRELATION_BUILDER_SERVICE, MessageBufferingConstants.START);

		log.info(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.MESSAGE_BUFFERING_SERVICE,
				MessageBufferingConstants.MESSAGE_CORRELATION_BUILDER_SERVICE, MessageBufferingConstants.DEBUG_LOG,
				MessageBufferingConstants.SERVICE,
				objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(messageEmployee));
		SuccessResponse successResponse = new SuccessResponse();

		List<ProcessInstance> processIntances = null;
		MessageCorrelationBuilder c1 = null;
		ObjectValue employeeMessageObjValue = null;
		try {
			switch (processName) {
			case MessageBufferingConstants.MESSAGE_THROW_CATCH_PROCESS:
				processIntances = runtimeService.createProcessInstanceQuery()
						.processInstanceBusinessKey(messageEmployee.getEmployeeId().toString(), processName).list();
				if (processIntances != null && !processIntances.isEmpty()) {
					for (ProcessInstance pi : processIntances) {

						employeeMessageObjValue = Variables.objectValue(messageEmployee)
								.serializationDataFormat("application/json").create();

						c1 = runtimeService.createMessageCorrelation(MessageBufferingConstants.MESSAGE_CATCH_EVENT)
								.processInstanceId(pi.getProcessInstanceId())
								.setVariable(MessageBufferingConstants.MESSAGE_EMPLOYEE, employeeMessageObjValue);

						c1.correlateWithResult();

						successResponse.setResponseCode("" + HttpStatus.OK.value());
						successResponse.setMessage(HttpStatus.OK.getReasonPhrase());
					}
				} else {
					successResponse.setResponseCode("" + HttpStatus.NOT_FOUND.value());
					successResponse.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
				}
				break;
			default:
				throw new MessageBufferingCustomException(
						new ErrorResponse(MessageBufferingExceptions.MESSAGE_CATCH_PROCESSNAME_NOT_FOUND,
								MessageBufferingConstants.MESSAGE_BUFFERING_SERVICE,
								MessageBufferingConstants.MESSAGE_CORRELATION_BUILDER_SERVICE, transactionId));
			}
		} catch (MismatchingMessageCorrelationException exc) {
			ErrorResponse errorResponse = new ErrorResponse(
					MessageBufferingExceptions.MISMATCHING_MESSAGE_CORRELATION_EXCEPTION,
					MessageBufferingConstants.MESSAGE_SEND_TASK, MessageBufferingConstants.EXECUTE, transactionId);

			MessageBufferingCustomException exception = new MessageBufferingCustomException(errorResponse);

			log.info(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.MESSAGE_SEND_TASK,
					MessageBufferingConstants.EXECUTE, MessageBufferingConstants.FAILED,
					MessageBufferingConstants.DELEGATE,
					objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exception));
			throw exception;
		}

		log.info(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.MESSAGE_BUFFERING_SERVICE,
				MessageBufferingConstants.MESSAGE_CORRELATION_BUILDER_SERVICE, MessageBufferingConstants.DEBUG_LOG,
				MessageBufferingConstants.SERVICE,
				objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(successResponse));
		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.MESSAGE_BUFFERING_SERVICE,
				MessageBufferingConstants.MESSAGE_CORRELATION_BUILDER_SERVICE, MessageBufferingConstants.END);
		return successResponse;
	}

	/**
	 * @implSpec Starts the process based on the paramaters specified
	 * @param processName
	 * @param businessKey
	 * @param employee
	 * @param transactionId
	 * @param retryCount
	 * @param retryDelay
	 */
	private void startProcess(String processName, String businessKey, Employee employee, String transactionId,
			Integer retryCount, Integer retryDelay) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put(MessageBufferingConstants.EMPLOYEE, employee);
		dataMap.put(MessageBufferingConstants.TRANSACTION_ID, transactionId);
		dataMap.put(MessageBufferingConstants.RETRY_COUNT, retryCount);
		dataMap.put(MessageBufferingConstants.RETRY_DELAY, retryDelay);
		dataMap.put(MessageBufferingConstants.RETRY_COUNT_STRING, messageBufferingUtil.getRetryCount(retryCount));
		dataMap.put(MessageBufferingConstants.RETRY_DELAY_STRING, messageBufferingUtil.getRetryDelayInSec(retryDelay));
		runtimeService.startProcessInstanceByKey(processName, businessKey, dataMap);
	}

	/**
	 * 
	 * @param responseCode
	 * @return HttpStatus
	 */
	@Override
	public HttpStatus getStatusCode(String responseCode) {
		HttpStatus status = null;
		switch (responseCode) {
		case MessageBufferingConstants.STATUS_200:
			status = HttpStatus.OK;
			break;
		case MessageBufferingConstants.STATUS_202:
			status = HttpStatus.ACCEPTED;
			break;
		case MessageBufferingConstants.STATUS_400:
			status = HttpStatus.BAD_REQUEST;
			break;
		case MessageBufferingConstants.STATUS_404:
			status = HttpStatus.NOT_FOUND;
			break;
		case MessageBufferingConstants.STATUS_500:
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			break;
		}
		return status;
	}

	@Override
	public SuccessResponse messageBufferRetryLogicService(String transactionId, Integer retryCount, Integer retryDelay,
			MessageBufferData messageBufferData) throws JsonProcessingException {

		Map<String, String> processVariableMap = new HashMap<>();

		runtimeService.startProcessInstanceByKey(MessageBufferingConstants.PROCESS_MESSAGE_BUFFERING_RETRY_LOGIC,
				messageBufferData.getBusinessKey(),
				getProcessVariable(messageBufferData, retryCount, retryDelay, transactionId));

		SuccessResponse successResponse = new SuccessResponse();
		successResponse.setResponseCode("" + HttpStatus.ACCEPTED.value());
		successResponse.setMessage(HttpStatus.ACCEPTED.getReasonPhrase());

		return successResponse;

	}

	private Map<String, Object> getProcessVariable(MessageBufferData messageBufferData, Integer retryCount,
			Integer retryDelay, String transactionId) throws JsonProcessingException {

		VariableMap variables = Variables.createVariables()
				.putValueTyped(MessageBufferingConstants.TRANSACTION_ID, Variables.stringValue(transactionId))
				.putValueTyped(MessageBufferingConstants.RETRY_COUNT, Variables.integerValue(retryCount))
				.putValueTyped(MessageBufferingConstants.RETRY_DELAY, Variables.integerValue(retryDelay))
				.putValueTyped(MessageBufferingConstants.RETRY_DELAY_STRING,
						Variables.stringValue(messageBufferingUtil.getRetryDelayInSec(retryDelay)))
				.putValueTyped(MessageBufferingConstants.TRANSACTION_ID, Variables.stringValue(transactionId));

		log.info("Variable Map Values : {}",
				objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(variables));

		Map<String, Object> processVariableMap = processVariableMap = new HashMap<>();
		if (messageBufferData != null) {
			processVariableMap.put(MessageBufferingConstants.PROCESS_VARIABLES, messageBufferData);
		}
		processVariableMap.put(MessageBufferingConstants.TRANSACTION_ID, transactionId);
		processVariableMap.put(MessageBufferingConstants.RETRY_COUNT, retryCount);
		processVariableMap.put(MessageBufferingConstants.RETRY_DELAY, retryDelay);
		processVariableMap.put(MessageBufferingConstants.RETRY_COUNT_STRING,
				messageBufferingUtil.getRetryCount(retryCount));
		processVariableMap.put(MessageBufferingConstants.RETRY_DELAY_STRING,
				messageBufferingUtil.getRetryDelayInSec(retryDelay));

		return processVariableMap;
	}

}
