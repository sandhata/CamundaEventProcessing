package com.sandhata.camunda.messagebuffering.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@lombok.Generated
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressInfo {
	
	@JsonProperty("doorNumber")
	private String doorNumber;
	
	@JsonProperty("streetName")
	private String streetName;
	
	@JsonProperty("addressLine2")
	private String addressLine2;
	
	@JsonProperty("postcode")
	private String postCode;
	
	@JsonProperty("city")
	private String city;
	
	@JsonProperty("country")
	private String country;

}
