package com.petersavitsky.jobcoinmixer.service;

import java.util.UUID;

public class MixingTransactionResult {

	private final UUID transactionId;
	private final boolean successful;

	public MixingTransactionResult(UUID transactionId, boolean successful) {
		this.transactionId = transactionId;
		this.successful = successful;
	}

	public UUID getTransactionId() {
		return transactionId;
	}

	public boolean isSuccessful() {
		return successful;
	}

}
