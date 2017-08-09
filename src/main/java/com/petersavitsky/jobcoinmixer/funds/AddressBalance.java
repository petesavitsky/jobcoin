package com.petersavitsky.jobcoinmixer.funds;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class AddressBalance {

	private final String address;
	private final Set<String> originAddresses = new HashSet<>();
	private BigDecimal balance;

	public AddressBalance(String address, Set<String> originAddresses, BigDecimal initialBalance) {
		this.address = address;
		this.balance = initialBalance;
		if (originAddresses != null) {
			this.originAddresses.addAll(originAddresses);
		}
	}

	public void addFunds(String originAddress, BigDecimal amount) {
		// check for negative amount?
		originAddresses.add(originAddress);
		balance = balance.add(amount);
	}

	public void removeFunds(BigDecimal amount) throws InsufficientFundsException {
		if (balance.compareTo(amount) < 0) {
			throw new InsufficientFundsException("Account [" + address + "] has balance of [" + balance.toPlainString()
					+ "] but requested [" + amount.toPlainString() + "]");
		}
		balance = balance.subtract(amount);
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public Set<String> getOriginAddresses() {
		return new HashSet<>(originAddresses);
	}

	public String getAddress() {
		return address;
	}

}
