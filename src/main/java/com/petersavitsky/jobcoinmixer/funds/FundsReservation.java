package com.petersavitsky.jobcoinmixer.funds;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FundsReservation {

	private final UUID reservationId;
	private final BigDecimal totalReservedAmount;
	private final Set<FundsReservationAddress> reservedAddresses;

	public FundsReservation(UUID reservationId, Set<FundsReservationAddress> reservedAddresses) {
		this.reservationId = reservationId;
		this.reservedAddresses = reservedAddresses;
		BigDecimal totalReserved = BigDecimal.ZERO;
		for (FundsReservationAddress address : reservedAddresses) {
			totalReserved = totalReserved.add(address.getReservedAmount());
		}
		this.totalReservedAmount = totalReserved;
	}

	public UUID getReservationId() {
		return reservationId;
	}

	public BigDecimal getTotalReservedAmount() {
		return totalReservedAmount;
	}

	public Set<FundsReservationAddress> getReservedAddresses() {
		return new HashSet<>(reservedAddresses);
	}

}
