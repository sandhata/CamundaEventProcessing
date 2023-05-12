package com.sandhata.camunda.messagebuffering.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandhata.camunda.messagebuffering.constants.MessageBufferingConstants;
import com.sandhata.camunda.messagebuffering.models.Employee;
import com.sandhata.camunda.messagebuffering.models.MessageBufferData;
import com.sandhata.camunda.messagebuffering.models.SuccessResponse;
import com.sandhata.camunda.messagebuffering.service.MessageBufferingService;

import lombok.extern.slf4j.Slf4j;

@RequestMapping(value = "/buffermessage")
@RestController
@Slf4j
public class MessageBufferingController {

	@Autowired
	private MessageBufferingService messageBufferingService;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * @apiNote Used to send the dummy message to get queued up in the message
	 *          event. Making them to wait for the trigger from the re-try logic
	 * @param employee
	 * @param transactionId
	 * @param processName
	 * @param retryCount
	 * @param retryDelay
	 * @return ResponseEntity<SuccessResponse>
	 * @throws JsonProcessingException
	 */
	@PostMapping(value = "/validate", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<SuccessResponse> validateRest(@RequestBody Employee employee,
			@RequestHeader(value = MessageBufferingConstants.TRANSACTION_ID) String transactionId,
			@RequestHeader(value = MessageBufferingConstants.PROCESS_NAME) String processName,
			@RequestParam(value = MessageBufferingConstants.RETRY_COUNT, defaultValue = "3") Integer retryCount,
			@RequestParam(value = MessageBufferingConstants.RETRY_DELAY, defaultValue = "5") Integer retryDelay)
			throws JsonProcessingException {
		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.MESSAGE_BUFFERING_CONTROLLER, MessageBufferingConstants.VALIDATE_REST,
				MessageBufferingConstants.START);
		log.info(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.MESSAGE_BUFFERING_CONTROLLER,
				MessageBufferingConstants.VALIDATE_REST, MessageBufferingConstants.SUCCESS,
				MessageBufferingConstants.REQUEST,
				objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(employee));
		SuccessResponse successResponse = messageBufferingService.validateMessageService(employee, transactionId,
				processName, retryCount, retryDelay);
		ResponseEntity<SuccessResponse> successResponseEntity = new ResponseEntity<SuccessResponse>(successResponse,
				getHeader(transactionId), messageBufferingService.getStatusCode(successResponse.getResponseCode()));
		log.info(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.MESSAGE_BUFFERING_CONTROLLER,
				MessageBufferingConstants.VALIDATE_REST, MessageBufferingConstants.SUCCESS,
				MessageBufferingConstants.RESPONSE,
				objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(successResponseEntity));
		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.MESSAGE_BUFFERING_CONTROLLER, MessageBufferingConstants.VALIDATE_REST,
				MessageBufferingConstants.END);
		return successResponseEntity;
	}

	/**
	 * @apiNote Used to trigger the message that are waiting in the message catch
	 *          event based on the process name that is defined. Invokes the message
	 *          catch event for the process names the api is invoked with.
	 * @param employeeMessage
	 * @param processName
	 * @param transactionId
	 * @return ResponseEntity<SuccessResponse>
	 * @throws JsonProcessingException
	 */
	@PostMapping(value = "/message/employee", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<SuccessResponse> messageEmployeeRest(@RequestBody Employee employeeMessage,
			@RequestParam(value = MessageBufferingConstants.PROCESS_NAME, required = true) String processName,
			@RequestHeader(value = MessageBufferingConstants.TRANSACTION_ID) String transactionId)
			throws JsonProcessingException {
		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.MESSAGE_BUFFERING_CONTROLLER, MessageBufferingConstants.MESSAGE_EMPLOYEE_REST,
				MessageBufferingConstants.START);
		log.info(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.MESSAGE_BUFFERING_CONTROLLER,
				MessageBufferingConstants.MESSAGE_EMPLOYEE_REST, MessageBufferingConstants.SUCCESS,
				MessageBufferingConstants.REQUEST,
				objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(employeeMessage));
		SuccessResponse successResponse = messageBufferingService.messageCorrelationEventService(transactionId,
				employeeMessage, processName);
		ResponseEntity<SuccessResponse> successResponseEntity = new ResponseEntity<SuccessResponse>(successResponse,
				getHeader(transactionId), messageBufferingService.getStatusCode(successResponse.getResponseCode()));
		log.info(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.MESSAGE_BUFFERING_CONTROLLER,
				MessageBufferingConstants.MESSAGE_EMPLOYEE_REST, MessageBufferingConstants.SUCCESS,
				MessageBufferingConstants.RESPONSE,
				objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(successResponseEntity));
		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.MESSAGE_BUFFERING_CONTROLLER, MessageBufferingConstants.MESSAGE_EMPLOYEE_REST,
				MessageBufferingConstants.END);
		return successResponseEntity;
	}

	/**
	 * @apiNote Used to invoke the re-try logic. The params re-try count and re-try
	 *          delay are used to specify the number of re-try attempts to be made
	 *          and also the delay between successive re-try attempts. The default
	 *          number of re-try count is 3 and the default re-try delay is 3
	 *          seconds
	 * @param messageBufferData
	 * @param transactionId
	 * @param retryCount
	 * @param retryDelay
	 * @return ResponseEntity<SuccessResponse>
	 * @throws JsonProcessingException
	 */
	@PostMapping(value = "/messagebuffer-retry", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<SuccessResponse> messageBufferRetryLogicRest(@RequestBody MessageBufferData messageBufferData,
			@RequestHeader(value = MessageBufferingConstants.TRANSACTION_ID) String transactionId,
			@RequestParam(value = MessageBufferingConstants.RETRY_COUNT, defaultValue = "3") Integer retryCount,
			@RequestParam(value = MessageBufferingConstants.RETRY_DELAY, defaultValue = "5") Integer retryDelay)
			throws JsonProcessingException {

		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.MESSAGE_BUFFERING_CONTROLLER,
				MessageBufferingConstants.MESSAGE_BUFFERING_RETRY_LOGICE_REST, MessageBufferingConstants.START);

		log.info(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.MESSAGE_BUFFERING_CONTROLLER,
				MessageBufferingConstants.MESSAGE_BUFFERING_RETRY_LOGICE_REST, MessageBufferingConstants.DEBUG_LOG,
				MessageBufferingConstants.REQUEST,
				objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(messageBufferData));

		SuccessResponse successResponse = messageBufferingService.messageBufferRetryLogicService(transactionId,
				retryCount, retryDelay, messageBufferData);

		ResponseEntity<SuccessResponse> responseEntity = new ResponseEntity<SuccessResponse>(successResponse,
				getHeader(transactionId), messageBufferingService.getStatusCode(successResponse.getResponseCode()));

		log.info(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.MESSAGE_BUFFERING_CONTROLLER,
				MessageBufferingConstants.MESSAGE_BUFFERING_RETRY_LOGICE_REST, MessageBufferingConstants.SUCCESS,
				MessageBufferingConstants.RESPONSE,
				objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseEntity));

		log.info(MessageBufferingConstants.START_END, transactionId,
				MessageBufferingConstants.MESSAGE_BUFFERING_CONTROLLER,
				MessageBufferingConstants.MESSAGE_BUFFERING_RETRY_LOGICE_REST, MessageBufferingConstants.END);

		return responseEntity;
	}

	/**
	 * @param transactionId generates a random unique transaction id
	 * @return HttpHeader
	 */
	private HttpHeaders getHeader(String transactionId) {
		HttpHeaders header = new HttpHeaders();
		header.set(MessageBufferingConstants.TRANSACTION_ID, transactionId);
		return header;
	}

}
