package com.kj.oneservice.common.integration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorDetails {
	
	@JsonProperty(value = "ERROR_CODE", required = true)
	private String errorCode;
	
	@JsonProperty(value = "ERROR_DESC", required = true)
	private String errorCodeDescription;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorCodeDescription() {
		return errorCodeDescription;
	}

	public void setErrorCodeDescription(String errorCodeDescription) {
		this.errorCodeDescription = errorCodeDescription;
	}
	

}
