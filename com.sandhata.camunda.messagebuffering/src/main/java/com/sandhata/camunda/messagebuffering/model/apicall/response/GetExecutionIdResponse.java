package com.sandhata.camunda.messagebuffering.model.apicall.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@lombok.Generated
@Getter
@NoArgsConstructor
@JsonInclude(content = JsonInclude.Include.NON_NULL,value = JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetExecutionIdResponse {
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("eventType")
	private String eventType;
	
	@JsonProperty("eventName")
	private String eventName;
	
	@JsonProperty("executionId")
	private String executionId;
	
	@JsonProperty("activityId")
	private String activityId;
	
	@JsonProperty("processInstanceId")
	private String processInstanceId;
	
	@JsonProperty("createdDate")
	private String createdDate;
	
	@JsonProperty("ended")
	private String ended;
	
	@JsonProperty("tenantId")
	private String tenantId;

}
