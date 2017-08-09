package com.petersavitsky.jobcoinmixer.funds;

import java.math.BigDecimal;

public class FundsReservationAddress {

	private final String fromAddress;
	private final String toAddress;
	private final BigDecimal reservedAmount;

	public FundsReservationAddress(String fromAddress, String toAddress, BigDecimal reservedAmount) {
		this.fromAddress = fromAddress;
		this.toAddress = toAddress;
		this.reservedAmount = reservedAmount;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public BigDecimal getReservedAmount() {
		return reservedAmount;
	}

}
