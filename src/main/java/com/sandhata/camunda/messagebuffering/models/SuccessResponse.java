package com.sandhata.camunda.messagebuffering.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@lombok.Generated
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse {

	@JsonProperty("status")
	private String responseCode;

	@JsonProperty("message")
	private String message;

}
