package com.petersavitsky.jobcoinmixer.ui;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenerateDepositAddressResponse {

	@JsonProperty("deposit_address")
	private String depositAddress;

	public String getDepositAddress() {
		return depositAddress;
	}

	public void setDepositAddress(String depositAddress) {
		this.depositAddress = depositAddress;
	}

}
