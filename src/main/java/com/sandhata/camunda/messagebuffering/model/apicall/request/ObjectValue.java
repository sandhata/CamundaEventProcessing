package com.sandhata.camunda.messagebuffering.model.apicall.request;

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
public class ObjectValue {
	
	@JsonProperty("value")
	private Object value;
	
	@JsonProperty("type")
	private String type;

}
