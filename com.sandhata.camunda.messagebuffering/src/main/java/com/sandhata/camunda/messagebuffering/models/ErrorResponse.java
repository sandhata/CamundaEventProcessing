package com.sandhata.camunda.messagebuffering.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sandhata.camunda.messagebuffering.exception.MessageBufferingExceptions;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@lombok.Generated
@Data
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
	
	@JsonProperty("Transaction Id")
	private String transactionId;

	@JsonProperty("Status Code")
	private String statusCode;

	@JsonProperty("Description")
	private String description;

	@JsonProperty("Error Message")
	private String errorMessage;

	@JsonProperty("Timestamp")
	private String timeStamp;

	@JsonProperty("Class Name")
	private String className;

	@JsonProperty("Method Name")
	private String methodName;

	public ErrorResponse(MessageBufferingExceptions excEnum, String className, String methodName, String transactionId) {
		this.transactionId=transactionId;
		this.statusCode = excEnum.getStatusCode();
		this.description = excEnum.getDescription();
		this.errorMessage = excEnum.getErrorMessage();
		this.timeStamp = new Date().toLocaleString();
		this.className = className;
		this.methodName = methodName;
	}
	
	public ErrorResponse(MessageBufferingExceptions excEnum, String errorMessage, String className, String methodName, String transactionId) {
		this.transactionId=transactionId;
		this.statusCode = excEnum.getStatusCode();
		this.description = excEnum.getDescription();
		this.errorMessage = errorMessage;
		this.timeStamp = new Date().toLocaleString();
		this.className = className;
		this.methodName = methodName;
	}
}