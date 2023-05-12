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
public class MessageBufferingCustomException extends RuntimeException{
	
	private String transactionId;
	
	private String statusCode;
	
	private String description;
	
	private String errorMessage;
	
	private String timeStamp;
	
	private String className;
	
	private String methodName;
	
	public MessageBufferingCustomException(MessageBufferingCustomException messageBufferingCustomException, String className, String methodName, String transactionId){
		this.transactionId=messageBufferingCustomException.getTransactionId();
		this.statusCode=messageBufferingCustomException.getStatusCode();
		this.description=messageBufferingCustomException.getDescription();
		this.errorMessage=messageBufferingCustomException.getErrorMessage();
		this.timeStamp=messageBufferingCustomException.getTimeStamp();
		this.className=className;
		this.methodName=methodName;
	}
	
	public MessageBufferingCustomException(ErrorResponse errorResponse) {
		this.transactionId=errorResponse.getTransactionId();
		this.statusCode=errorResponse.getStatusCode();
		this.description=errorResponse.getDescription();
		this.errorMessage=errorResponse.getErrorMessage();
		this.timeStamp=errorResponse.getTimeStamp();
		this.className=errorResponse.getClassName();
		this.methodName=errorResponse.getMethodName();
	}

}
