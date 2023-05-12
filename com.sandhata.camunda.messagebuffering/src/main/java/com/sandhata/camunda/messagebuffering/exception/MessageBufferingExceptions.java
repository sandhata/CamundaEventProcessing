package com.sandhata.camunda.messagebuffering.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@lombok.Generated
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum MessageBufferingExceptions {
	
	MISMATCHING_MESSAGE_CORRELATION_EXCEPTION("400", "Bad Request", "Mismatching message correlation"), 
	MESSAGE_CATCH_PROCESSNAME_NOT_FOUND("404", "Not Found", "Match not found for message catch process name"),
	MISSING_BUSINESS_KEY("400", "Bad Request", "Missing Business key value"),
	MISSING_MESSAGE_NAME("400", "Bad Request", "Missing Message name to be triggered"),
	BAD_REQUEST_FETCHING_EXECUTION_ID("400", "Bad Request", "Error while fetching Execution ID"),
	BAD_REQUEST_TRIGGERING_MESSAGE_CATCH_EVENT("400", "Bad Request", "Error while triggering message catch event"),
	INTERNAL_SERVER_ERROR_FETCHING_EXECUTION_ID("500", "Internal Server Error", "Error while fetching Execution ID"),
	INTERNAL_SERVER_ERROR_TRIGGERING_MESSAGE_CATCH_EVENT("500", "Internal Server Error", "Error while triggering message catch event");
	
	private String statusCode;
	private String description;
	private String errorMessage;

}
