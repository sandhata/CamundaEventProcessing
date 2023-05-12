package com.sandhata.camunda.messagebuffering.model.apicall.request;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@lombok.Generated
@Data
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariableRequest {

	@JsonProperty("variables")
	private Map<String, ObjectValue> processVariables;

}
