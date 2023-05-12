package com.sandhata.camunda.messagebuffering.exception.handler;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandhata.camunda.messagebuffering.constants.MessageBufferingConstants;
import com.sandhata.camunda.messagebuffering.exception.MessageBufferingCustomException;
import com.sandhata.camunda.messagebuffering.models.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class MessageBufferingExceptionHandler {

	@Autowired
	private ObjectMapper objectMapper;

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> genericExceptionHandler(Exception exc) throws JsonProcessingException {
		log.info(MessageBufferingConstants.START_END, MessageBufferingConstants.MESSAGE_BUFFERING_EXCEPTION_HANDLER,
				MessageBufferingConstants.GENERIC_EXCEPTION_HANDLER, MessageBufferingConstants.START);
		exc.printStackTrace();
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		errorResponse.setErrorMessage(exc.getMessage());
		errorResponse.setTimeStamp(new Date().toGMTString());
		errorResponse.setClassName(MessageBufferingConstants.UNKNOWN);
		errorResponse.setTransactionId(MessageBufferingConstants.UNKNOWN);
		ResponseEntity<ErrorResponse> errorResponseEntity = new ResponseEntity<ErrorResponse>(errorResponse,
				HttpStatus.INTERNAL_SERVER_ERROR);
		log.error("Error : \n {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exc));
		log.error(MessageBufferingConstants.OUT, MessageBufferingConstants.MESSAGE_BUFFERING_EXCEPTION_HANDLER,
				MessageBufferingConstants.GENERIC_EXCEPTION_HANDLER, MessageBufferingConstants.FAILED,
				MessageBufferingConstants.RESPONSE,
				objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorResponseEntity));
		log.info(MessageBufferingConstants.START_END, MessageBufferingConstants.MESSAGE_BUFFERING_EXCEPTION_HANDLER,
				MessageBufferingConstants.GENERIC_EXCEPTION_HANDLER, MessageBufferingConstants.END);
		return errorResponseEntity;
	}

	@ExceptionHandler(MessageBufferingCustomException.class)
	public ResponseEntity<ErrorResponse> MessageBufferingCustomExceptionHandler(MessageBufferingCustomException exc)
			throws JsonProcessingException {
		log.info(MessageBufferingConstants.START_END, MessageBufferingConstants.MESSAGE_BUFFERING_EXCEPTION_HANDLER,
				MessageBufferingConstants.MESSAGE_BUFFERING_CUSTOM_EXCEPTION_HANDLER, MessageBufferingConstants.START);

		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		errorResponse.setErrorMessage(exc.getErrorMessage());
		errorResponse.setTimeStamp(new Date().toGMTString());
		errorResponse.setTransactionId(exc.getTransactionId());
		ResponseEntity<ErrorResponse> errorResponseEntity = new ResponseEntity<ErrorResponse>(errorResponse,
				HttpStatus.BAD_REQUEST);
		log.error("Error : \n {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exc));
		log.error(MessageBufferingConstants.OUT, MessageBufferingConstants.MESSAGE_BUFFERING_EXCEPTION_HANDLER,
				MessageBufferingConstants.MESSAGE_BUFFERING_CUSTOM_EXCEPTION_HANDLER, MessageBufferingConstants.FAILED,
				MessageBufferingConstants.RESPONSE,
				objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorResponseEntity));
		log.info(MessageBufferingConstants.START_END, MessageBufferingConstants.MESSAGE_BUFFERING_EXCEPTION_HANDLER,
				MessageBufferingConstants.MESSAGE_BUFFERING_CUSTOM_EXCEPTION_HANDLER, MessageBufferingConstants.END);
		return errorResponseEntity;
	}

}
