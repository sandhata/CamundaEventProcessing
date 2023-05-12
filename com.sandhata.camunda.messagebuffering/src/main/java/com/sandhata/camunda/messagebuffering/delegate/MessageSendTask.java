package com.sandhata.camunda.messagebuffering.delegate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandhata.camunda.messagebuffering.constants.MessageBufferingConstants;
import com.sandhata.camunda.messagebuffering.exception.MessageBufferingCustomException;
import com.sandhata.camunda.messagebuffering.exception.MessageBufferingExceptions;
import com.sandhata.camunda.messagebuffering.models.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Component(value = "MessageSendTask")
@Slf4j
public class MessageSendTask implements JavaDelegate {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RuntimeService runtimeService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		try {
			String transactionId = objectMapper
					.convertValue(execution.getVariable(MessageBufferingConstants.TRANSACTION_ID), String.class);

			Integer retryCount = objectMapper.convertValue(execution.getVariable(MessageBufferingConstants.RETRY_COUNT),
					Integer.class);

			Integer retryDelay = objectMapper.convertValue(execution.getVariable(MessageBufferingConstants.RETRY_DELAY),
					Integer.class);

			String businessKey = objectMapper
					.convertValue(execution.getVariable(MessageBufferingConstants.BUSINESS_KEY), String.class);

			String retryCountString = objectMapper
					.convertValue(execution.getVariable(MessageBufferingConstants.RETRY_COUNT_STRING), String.class);

			String retryDelayString = objectMapper
					.convertValue(execution.getVariable(MessageBufferingConstants.RETRY_DELAY_STRING), String.class);

			log.info(MessageBufferingConstants.START_END, transactionId, MessageBufferingConstants.MESSAGE_SEND_TASK,
					MessageBufferingConstants.EXECUTE, MessageBufferingConstants.START);

//			Employee employee = objectMapper.convertValue(execution.getVariable(MessageBufferingConstants.EMPLOYEE),
//					Employee.class);

			Object employee = execution.getVariable(MessageBufferingConstants.EMPLOYEE);

			ObjectValue employeeMessageObjValue = Variables.objectValue(employee)
					.serializationDataFormat("application/json").create();

			log.info(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.MESSAGE_SEND_TASK,
					MessageBufferingConstants.EXECUTE, MessageBufferingConstants.DEBUG_LOG,
					MessageBufferingConstants.DELEGATE,
					objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(employeeMessageObjValue));

			log.info(MessageBufferingConstants.START_END, transactionId, MessageBufferingConstants.MESSAGE_SEND_TASK,
					MessageBufferingConstants.EXECUTE, MessageBufferingConstants.END);

//			throw new MismatchingMessageCorrelationException("Validation");

//			List<ProcessInstance> processIntances = runtimeService.createProcessInstanceQuery()
//					.processInstanceBusinessKey(employee.getEmployeeId().toString(),
//							MessageBufferingConstants.MESSAGE_RECEIVE_TASK)
//					.list();

			List<ProcessInstance> processIntances = runtimeService.createProcessInstanceQuery()
					.processInstanceBusinessKey(businessKey, MessageBufferingConstants.MESSAGE_RECEIVE_TASK).list();

//			List<ProcessInstance> processIntances = runtimeService.createProcessInstanceQuery()
//					.processInstanceBusinessKey(employee.getEmployeeId().toString()).list();

			try {
				if (processIntances != null && !processIntances.isEmpty()) {
					for (ProcessInstance pi : processIntances) {
						MessageCorrelationBuilder c1 = runtimeService
								.createMessageCorrelation(MessageBufferingConstants.MESSAGE_RECEIVE_TASK)
								.processInstanceBusinessKey(MessageBufferingConstants.MESSAGE_RECEIVE_TASK)
								.setVariable(MessageBufferingConstants.MESSAGE_EMPLOYEE, employeeMessageObjValue)
								.setVariable(MessageBufferingConstants.RETRY_COUNT, retryCount)
								.setVariable(MessageBufferingConstants.RETRY_DELAY, retryDelay)
								.setVariable(MessageBufferingConstants.RETRY_COUNT_STRING, retryCountString)
								.setVariable(MessageBufferingConstants.RETRY_DELAY_STRING, retryDelayString)
								.setVariable(MessageBufferingConstants.TRANSACTION_ID, transactionId);
						c1.correlate();

						ProcessInstance pi2 = runtimeService.startProcessInstanceByKey("processWaitingInReceiveTask");
						EventSubscription subscription = runtimeService.createEventSubscriptionQuery()
								.processInstanceId(pi2.getId()).eventType("message").singleResult();
						runtimeService.messageEventReceived(subscription.getEventName(), subscription.getExecutionId());

//						List<Execution> execution2 = runtimeService.createExecutionQuery()
//								.processInstanceBusinessKey(employee.getEmployeeId()).active().list();

						List<Execution> execution2 = runtimeService.createExecutionQuery()
								.processInstanceBusinessKey(businessKey).active().list();

						Map<String, Object> processVariables = new HashMap<>();

						processVariables.put(MessageBufferingConstants.MESSAGE_EMPLOYEE, employeeMessageObjValue);
						processVariables.put(MessageBufferingConstants.RETRY_COUNT, retryCount);
						processVariables.put(MessageBufferingConstants.RETRY_DELAY, retryDelay);
						processVariables.put(MessageBufferingConstants.RETRY_COUNT_STRING, retryCountString);
						processVariables.put(MessageBufferingConstants.RETRY_DELAY_STRING, retryDelayString);
						processVariables.put(MessageBufferingConstants.TRANSACTION_ID, transactionId);

//						runtimeService.correlateMessage(MessageBufferingConstants.MESSAGE_RECEIVE_TASK,
//								employee.getEmployeeId(), processVariables);
//						runtimeService.messageEventReceived(MessageBufferingConstants.MESSAGE_RECEIVE_TASK,
//								employee.getEmployeeId(), processVariables);

						runtimeService.correlateMessage(MessageBufferingConstants.MESSAGE_RECEIVE_TASK, businessKey,
								processVariables);

						runtimeService.messageEventReceived(MessageBufferingConstants.MESSAGE_RECEIVE_TASK, businessKey,
								processVariables);

						runtimeService.signal(execution2.get(0).getId(), processVariables);
					}
				}
			} catch (MismatchingMessageCorrelationException exc) {
				ErrorResponse errorResponse = new ErrorResponse(
						MessageBufferingExceptions.MISMATCHING_MESSAGE_CORRELATION_EXCEPTION,
						MessageBufferingConstants.MESSAGE_SEND_TASK, MessageBufferingConstants.EXECUTE, transactionId);

				execution.setVariable(MessageBufferingConstants.RETRY_ERROR, MessageBufferingConstants.TRUE);

				BpmnError bpmnError = new BpmnError("" + HttpStatus.BAD_REQUEST.value(),
						new MessageBufferingCustomException(errorResponse));

				log.info(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.MESSAGE_SEND_TASK,
						MessageBufferingConstants.EXECUTE, MessageBufferingConstants.FAILED,
						MessageBufferingConstants.DELEGATE,
						objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bpmnError));
				throw bpmnError;
			}

		} catch (MismatchingMessageCorrelationException exc) {
			execution.setVariable(MessageBufferingConstants.RETRY_ERROR, MessageBufferingConstants.TRUE);
			throw new BpmnError(exc.getMessage());
		}
	}

}
