package com.petersavitsky.jobcoinmixer.service;

import java.util.UUID;
import java.util.concurrent.Callable;

public class ScheduledMixingTransaction implements Callable<MixingTransactionResult> {

	private final UUID reservationId;

	public ScheduledMixingTransaction(UUID reservationId) {
		this.reservationId = reservationId;
	}

	public MixingTransactionResult call() throws Exception {
		// transfer from the assigned house account to the output account
		// return result indicating success or failure
		return new MixingTransactionResult(reservationId, false);
	}

}
