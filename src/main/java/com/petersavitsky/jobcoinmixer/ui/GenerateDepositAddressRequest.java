package com.petersavitsky.jobcoinmixer.ui;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenerateDepositAddressRequest {

	@JsonProperty("addresses")
	private List<String> addresses;

	public List<String> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<String> addresses) {
		this.addresses = addresses;
	}

}
