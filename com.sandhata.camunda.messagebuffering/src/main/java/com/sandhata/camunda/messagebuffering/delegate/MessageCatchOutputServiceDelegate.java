package com.sandhata.camunda.messagebuffering.delegate;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandhata.camunda.messagebuffering.constants.MessageBufferingConstants;
import com.sandhata.camunda.messagebuffering.models.Employee;

import lombok.extern.slf4j.Slf4j;

@Component(value = "MessageCatchOutputService")
@Slf4j
public class MessageCatchOutputServiceDelegate implements JavaDelegate {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		String transactionId = objectMapper
				.convertValue(execution.getVariable(MessageBufferingConstants.TRANSACTION_ID), String.class);

		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.MESSAGE_CATCH_OUTPUT_SERVICE_DELEGATE, MessageBufferingConstants.EXECUTE,
				MessageBufferingConstants.START);

		log.info("Employee Value from Execution object :\n{}", objectMapper.writerWithDefaultPrettyPrinter()
				.writeValueAsString(execution.getVariable(MessageBufferingConstants.EMPLOYEE)));

		String employeeString = null;
		Employee employee = null;
		String messageEmployeeString = null;

		if (execution.hasVariable(MessageBufferingConstants.EMPLOYEE)) {
			log.info("Employee String : \n{}", execution.getVariable(MessageBufferingConstants.EMPLOYEE));

			employeeString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper
					.convertValue(execution.getVariable(MessageBufferingConstants.EMPLOYEE), Employee.class));
		}

		if (StringUtils.isNotBlank(employeeString)) {
			log.info("Employee Value from emp1 object :\n{}",
					objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(employeeString));

			employee = objectMapper.readValue(employeeString, Employee.class);
		}

		if (execution.hasVariable(MessageBufferingConstants.MESSAGE_EMPLOYEE)) {
			log.info("Message Employee String : \n{}",
					execution.getVariable(MessageBufferingConstants.MESSAGE_EMPLOYEE));

			messageEmployeeString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper
					.convertValue(execution.getVariable(MessageBufferingConstants.MESSAGE_EMPLOYEE), Employee.class));
		}

		if (StringUtils.isNotBlank(messageEmployeeString)) {
			Employee messageEmployee = objectMapper.readValue(messageEmployeeString, Employee.class);

			execution.setVariable(MessageBufferingConstants.EMPLOYEE_MESSAGE_ID, messageEmployee.getEmployeeId());

			log.info(MessageBufferingConstants.OUT, transactionId,
					MessageBufferingConstants.MESSAGE_CATCH_OUTPUT_SERVICE_DELEGATE, MessageBufferingConstants.EXECUTE,
					MessageBufferingConstants.DEBUG_LOG, MessageBufferingConstants.DELEGATE,
					objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(messageEmployee));
		}

		log.info(MessageBufferingConstants.OUT, transactionId,
				MessageBufferingConstants.MESSAGE_CATCH_OUTPUT_SERVICE_DELEGATE, MessageBufferingConstants.EXECUTE,
				MessageBufferingConstants.DEBUG_LOG, MessageBufferingConstants.DELEGATE,
				objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(employee));

		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.MESSAGE_CATCH_OUTPUT_SERVICE_DELEGATE, MessageBufferingConstants.EXECUTE,
				MessageBufferingConstants.END);

	}

}
