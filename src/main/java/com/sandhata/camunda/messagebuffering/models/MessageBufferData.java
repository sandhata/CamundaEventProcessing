package com.sandhata.camunda.messagebuffering.models;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sandhata.camunda.messagebuffering.model.apicall.request.VariableRequest;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@lombok.Generated
@Data
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageBufferData {

	@JsonProperty("processInstanceId")
	private String processInstanceId;

	@NotBlank
	@JsonProperty("businessKey")
	private String businessKey;

	@NotBlank
	@JsonProperty("messageName")
	private String messageName;

	@JsonProperty("processVariables")
	private VariableRequest processVariables;

}
