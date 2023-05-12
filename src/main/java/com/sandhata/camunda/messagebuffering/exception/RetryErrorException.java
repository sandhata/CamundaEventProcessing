package com.sandhata.camunda.messagebuffering.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sandhata.camunda.messagebuffering.models.ErrorResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@lombok.Generated
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class RetryErrorException extends RuntimeException {

	private String statusCode;

	private String description;

	private String errorMessage;

	private String timeStamp;

	private String className;

	private String methodName;

	public RetryErrorException(RetryErrorException retryException, String className, String methodName) {
		this.statusCode = retryException.getStatusCode();
		this.description = retryException.getDescription();
		this.errorMessage = retryException.getErrorMessage();
		this.timeStamp = retryException.getTimeStamp();
		this.className = className;
		this.methodName = methodName;
	}

	public RetryErrorException(ErrorResponse errorResponse) {
		this.statusCode = errorResponse.getStatusCode();
		this.description = errorResponse.getDescription();
		this.errorMessage = errorResponse.getErrorMessage();
		this.timeStamp = errorResponse.getTimeStamp();
		this.className = errorResponse.getClassName();
		this.methodName = errorResponse.getMethodName();
	}

}
