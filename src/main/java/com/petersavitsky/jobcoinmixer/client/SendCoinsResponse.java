package com.petersavitsky.jobcoinmixer.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SendCoinsResponse {

	@JsonProperty("status")
	private String status;
	
	@JsonProperty("error")
	private String error;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
