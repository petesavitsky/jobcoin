package com.petersavitsky.jobcoinmixer.ui;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenerateDepositAddressRequest {

	@JsonProperty("email")
	private String email;

	@JsonProperty("addresses")
	private List<String> addresses;

	@JsonProperty("timeframe_value")
	private int timeframeValue;

	@JsonProperty("timeframe_unit")
	private String timeframeUnit;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<String> addresses) {
		this.addresses = addresses;
	}

	public int getTimeframeValue() {
		return timeframeValue;
	}

	public void setTimeframeValue(int timeframeValue) {
		this.timeframeValue = timeframeValue;
	}

	public String getTimeframeUnit() {
		return timeframeUnit;
	}

	public void setTimeframeUnit(String timeframeUnit) {
		this.timeframeUnit = timeframeUnit;
	}

}
