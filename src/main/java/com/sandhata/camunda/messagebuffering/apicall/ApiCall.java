package com.sandhata.camunda.messagebuffering.apicall;

import java.net.URI;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandhata.camunda.messagebuffering.config.ApplicationConfig;
import com.sandhata.camunda.messagebuffering.constants.MessageBufferingConstants;
import com.sandhata.camunda.messagebuffering.exception.MessageBufferingCustomException;
import com.sandhata.camunda.messagebuffering.exception.MessageBufferingExceptions;
import com.sandhata.camunda.messagebuffering.exception.RetryErrorException;
import com.sandhata.camunda.messagebuffering.model.apicall.request.VariableRequest;
import com.sandhata.camunda.messagebuffering.model.apicall.response.GetExecutionIdResponse;
import com.sandhata.camunda.messagebuffering.models.ErrorResponse;
import com.sandhata.camunda.messagebuffering.models.MessageBufferData;
import com.sandhata.camunda.messagebuffering.models.SuccessResponse;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ApiCall {

	@Autowired
	private ApplicationConfig appConfig;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * @param messageBufferData Input payload to be passed to the message event
	 * @param transactionId     unique string to trace the request and response
	 * @return executionId String
	 * @throws JsonProcessingException
	 */
	public String getExecutionId(MessageBufferData messageBufferData, String transactionId)
			throws JsonProcessingException {

		log.info(MessageBufferingConstants.START_END, transactionId, MessageBufferingConstants.API_CALL,
				MessageBufferingConstants.GET_EXECUTION_ID, MessageBufferingConstants.START);
		String executionId = null;
		try {
			HttpHeaders headers = getHeader();
			HttpEntity<?> entity = new HttpEntity<>(null, headers);
			String basePath = appConfig.getCamundarestapibasepath();
			String executionIdUrlPath = appConfig.getExecutionidurlpath();

			final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(basePath).path(executionIdUrlPath);
//					.queryParam(MessageBufferingConstants.PROCESS_INSTANCE_ID, messageBufferData.getProcessInstanceId())
//					.queryParam(MessageBufferingConstants.MESSAGE_EVENT_SUBSCRIPTION_NAME,
//							messageBufferData.getMessageName());

			if (StringUtils.isNotBlank(messageBufferData.getProcessInstanceId())) {
				builder.queryParam(MessageBufferingConstants.PROCESS_INSTANCE_ID,
						messageBufferData.getProcessInstanceId());
			}

			if (messageBufferData.getBusinessKey() != null && !messageBufferData.getBusinessKey().isEmpty()) {
				builder.queryParam(MessageBufferingConstants.BUSINESS_KEY, messageBufferData.getBusinessKey());
			} else {
				throw new MessageBufferingCustomException(new ErrorResponse(
						MessageBufferingExceptions.MISSING_BUSINESS_KEY, MessageBufferingConstants.API_CALL,
						MessageBufferingConstants.GET_EXECUTION_ID, transactionId));
			}

			if (StringUtils.isNotBlank(messageBufferData.getMessageName())) {
				builder.queryParam(MessageBufferingConstants.MESSAGE_EVENT_SUBSCRIPTION_NAME,
						messageBufferData.getMessageName());
			} else {
				throw new MessageBufferingCustomException(new ErrorResponse(
						MessageBufferingExceptions.MISSING_MESSAGE_NAME, MessageBufferingConstants.API_CALL,
						MessageBufferingConstants.GET_EXECUTION_ID, transactionId));
			}

			URI url = builder.build(true).toUri();

			ResponseEntity<List<GetExecutionIdResponse>> response = restTemplate.exchange(url.toString(),
					HttpMethod.GET, entity, new ParameterizedTypeReference<List<GetExecutionIdResponse>>() {
					});

			if (response.getStatusCode().is2xxSuccessful() && !response.getBody().isEmpty()) {
				executionId = response.getBody().get(0).getId();
			} else {
				String errorString = "Error while fetching Execution ID : No Execution Id found for the given process instance :"
						+ messageBufferData.getProcessInstanceId();

				if (!messageBufferData.getBusinessKey().isEmpty()) {
					errorString += " and business Key :" + messageBufferData.getBusinessKey();
				}

				ErrorResponse errorResponse = new ErrorResponse(
						MessageBufferingExceptions.MISMATCHING_MESSAGE_CORRELATION_EXCEPTION, errorString,
						MessageBufferingConstants.API_CALL, MessageBufferingConstants.GET_EXECUTION_ID, transactionId);

				throw new MessageBufferingCustomException(errorResponse);
			}
		} catch (HttpClientErrorException exc) {

			ErrorResponse response = new ErrorResponse(MessageBufferingExceptions.BAD_REQUEST_FETCHING_EXECUTION_ID,
					MessageBufferingConstants.API_CALL, MessageBufferingConstants.GET_EXECUTION_ID, transactionId);

			log.error(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.API_CALL,
					MessageBufferingConstants.GET_EXECUTION_ID, MessageBufferingConstants.FAILED,
					MessageBufferingConstants.BACKEND,
					objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));

			throw new RetryErrorException(response);
		} catch (Exception exc) {

			ErrorResponse response = new ErrorResponse(
					MessageBufferingExceptions.INTERNAL_SERVER_ERROR_FETCHING_EXECUTION_ID,
					MessageBufferingConstants.API_CALL, MessageBufferingConstants.GET_EXECUTION_ID, transactionId);

			log.error(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.API_CALL,
					MessageBufferingConstants.GET_EXECUTION_ID, MessageBufferingConstants.FAILED,
					MessageBufferingConstants.BACKEND,
					objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));

			throw exc;
		}

		log.info(MessageBufferingConstants.START_END, transactionId, MessageBufferingConstants.API_CALL,
				MessageBufferingConstants.GET_EXECUTION_ID, MessageBufferingConstants.END);

		return executionId;
	}

	/**
	 * @apiNote To Validate re-try Scenario. 1. The whole scenario will end up in
	 *          re-try only in case of a HTTP Client Error Exception. 2. When we
	 *          throw a http client error then we translate the hTTP client error
	 *          into a custome error (RetryErrorException) because of which it goes
	 *          into re-try cycle.
	 * 
	 * @param messageBufferData Input payload to be passed to the message event
	 * @param transactionId     unique string to trace the request and response
	 * @param executionId       The values that was fetched based on the message
	 *                          name and the business key
	 * @return SuccessResponse Object (status, message)
	 * @throws JsonProcessingException
	 */
	public SuccessResponse triggerMessageCatchEvent(MessageBufferData messageBufferData, String transactionId,
			String executionId) throws JsonProcessingException {

		log.info(MessageBufferingConstants.START_END, transactionId, MessageBufferingConstants.API_CALL,
				MessageBufferingConstants.TRIGGER_MESSAGE_CATCH_EVENT, MessageBufferingConstants.START);

		SuccessResponse successResponse = new SuccessResponse();

		try {
			HttpEntity<VariableRequest> entity = new HttpEntity<>(messageBufferData.getProcessVariables());

			log.info("Message trigger catch event Entity : {}",
					objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity));

			String basePath = appConfig.getCamundarestapibasepath();
			String executionIdUrlPath = appConfig.getTriggermessageurlpath();

			executionIdUrlPath = java.text.MessageFormat.format(executionIdUrlPath, executionId,
					messageBufferData.getMessageName());

			final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(basePath).path(executionIdUrlPath);
			URI url = builder.build(true).toUri();

			log.info(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.API_CALL,
					MessageBufferingConstants.TRIGGER_MESSAGE_CATCH_EVENT, MessageBufferingConstants.DEBUG_LOG,
					MessageBufferingConstants.URL,
					objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(url));

			log.info(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.API_CALL,
					MessageBufferingConstants.TRIGGER_MESSAGE_CATCH_EVENT, MessageBufferingConstants.BACKEND,
					MessageBufferingConstants.REQUEST,
					objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity));

			if (true) {
				throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
			}

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				successResponse.setResponseCode("" + HttpStatus.OK.value());
				successResponse.setMessage("Triggered Successfully");

				log.info(MessageBufferingConstants.START_END, transactionId, MessageBufferingConstants.API_CALL,
						MessageBufferingConstants.TRIGGER_MESSAGE_CATCH_EVENT, MessageBufferingConstants.END);

			} else {
				ErrorResponse errorResponse = new ErrorResponse(
						MessageBufferingExceptions.BAD_REQUEST_TRIGGERING_MESSAGE_CATCH_EVENT,
						MessageBufferingConstants.API_CALL, MessageBufferingConstants.TRIGGER_MESSAGE_CATCH_EVENT,
						transactionId);

				log.error(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.API_CALL,
						MessageBufferingConstants.TRIGGER_MESSAGE_CATCH_EVENT, MessageBufferingConstants.FAILED,
						MessageBufferingConstants.BACKEND,
						objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorResponse));

				log.info(MessageBufferingConstants.START_END, transactionId, MessageBufferingConstants.API_CALL,
						MessageBufferingConstants.TRIGGER_MESSAGE_CATCH_EVENT, MessageBufferingConstants.END);

				throw new RetryErrorException(errorResponse);
			}
		} catch (HttpClientErrorException exc) {
			ErrorResponse errorResponse = new ErrorResponse(
					MessageBufferingExceptions.BAD_REQUEST_TRIGGERING_MESSAGE_CATCH_EVENT,
					MessageBufferingConstants.API_CALL, MessageBufferingConstants.TRIGGER_MESSAGE_CATCH_EVENT,
					transactionId);
			log.error(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.API_CALL,
					MessageBufferingConstants.TRIGGER_MESSAGE_CATCH_EVENT, MessageBufferingConstants.FAILED,
					MessageBufferingConstants.BACKEND,
					objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorResponse));
			throw new RetryErrorException(errorResponse);
		} catch (Exception exc) {
			ErrorResponse errorResponse = new ErrorResponse(
					MessageBufferingExceptions.INTERNAL_SERVER_ERROR_TRIGGERING_MESSAGE_CATCH_EVENT,
					MessageBufferingConstants.API_CALL, MessageBufferingConstants.TRIGGER_MESSAGE_CATCH_EVENT,
					transactionId);
			log.error(MessageBufferingConstants.OUT, transactionId, MessageBufferingConstants.API_CALL,
					MessageBufferingConstants.TRIGGER_MESSAGE_CATCH_EVENT, MessageBufferingConstants.FAILED,
					MessageBufferingConstants.BACKEND,
					objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorResponse));
			throw new MessageBufferingCustomException(errorResponse);
		}
		return successResponse;
	}

	private HttpHeaders getHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		return headers;
	}

}
