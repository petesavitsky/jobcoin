package com.petersavitsky.jobcoinmixer.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Transaction {

	@JsonProperty("timestamp")
	private String timestamp;

	@JsonProperty("fromAddress")
	private String fromAddress;

	@JsonProperty("toAddress")
	private String toAddress;

	@JsonProperty("amount")
	private String amount;

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
}
