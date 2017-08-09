package com.petersavitsky.jobcoinmixer.client;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddressInfo {

	@JsonProperty("balance")
	private String balance;
	
	@JsonProperty("transactions")
	private List<Transaction> transactions;

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
}
