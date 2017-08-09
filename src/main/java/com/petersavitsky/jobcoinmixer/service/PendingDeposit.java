package com.petersavitsky.jobcoinmixer.service;

import java.util.HashSet;
import java.util.Set;

public class PendingDeposit {

	private final String depositAddress;
	private final Set<String> outputAddresses;
	private final long expirationDateMillis;

	public PendingDeposit(String depositAddress, Set<String> outputAddresses, long expirationDateMillis) {
		this.depositAddress = depositAddress;
		this.outputAddresses = outputAddresses;
		this.expirationDateMillis = expirationDateMillis;
	}

	public String getDepositAddress() {
		return depositAddress;
	}

	public Set<String> getOutputAddresses() {
		return new HashSet<>(outputAddresses);
	}

	public long getExpirationDateMillis() {
		return expirationDateMillis;
	}

}
