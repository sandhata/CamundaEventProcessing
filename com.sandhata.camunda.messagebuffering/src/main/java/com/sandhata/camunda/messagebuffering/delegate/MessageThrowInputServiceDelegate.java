package com.sandhata.camunda.messagebuffering.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandhata.camunda.messagebuffering.constants.MessageBufferingConstants;
import com.sandhata.camunda.messagebuffering.models.Employee;

import lombok.extern.slf4j.Slf4j;

@Component(value = "MessageThrowInputService")
@Slf4j
public class MessageThrowInputServiceDelegate implements JavaDelegate {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String transactionId = objectMapper
				.convertValue(execution.getVariable(MessageBufferingConstants.TRANSACTION_ID), String.class);
		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.MESSAGE_THROW_INPUT_SERVICE_DELEGATE, MessageBufferingConstants.EXECUTE,
				MessageBufferingConstants.START);
		Employee employee = objectMapper.convertValue(execution.getVariable(MessageBufferingConstants.EMPLOYEE),
				Employee.class);

		log.info(MessageBufferingConstants.OUT, transactionId,
				MessageBufferingConstants.MESSAGE_THROW_INPUT_SERVICE_DELEGATE, MessageBufferingConstants.EXECUTE,
				MessageBufferingConstants.DEBUG_LOG, MessageBufferingConstants.DELEGATE,
				objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(employee));

		employee.setAge("21");

		ObjectValue employeeObjValue = Variables.objectValue(employee).serializationDataFormat("application/json")
				.create();

		execution.setVariable(MessageBufferingConstants.EMPLOYEE, employeeObjValue);
		execution.setVariable(MessageBufferingConstants.EMPLOYEE_ID, employee.getEmployeeId());
		execution.setVariable(MessageBufferingConstants.RETRY_COUNT, ""+MessageBufferingConstants.THREE.intValue());
		
		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.MESSAGE_THROW_INPUT_SERVICE_DELEGATE, MessageBufferingConstants.EXECUTE,
				MessageBufferingConstants.END);
	}

}