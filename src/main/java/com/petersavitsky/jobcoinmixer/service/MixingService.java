package com.petersavitsky.jobcoinmixer.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.petersavitsky.jobcoinmixer.client.AddressInfo;
import com.petersavitsky.jobcoinmixer.client.JobcoinClient;
import com.petersavitsky.jobcoinmixer.client.JobcoinClientException;
import com.petersavitsky.jobcoinmixer.funds.FundsService;

import groovy.lang.Singleton;

@Service
@Singleton
public class MixingService {

	private final FundsService fundsService;

	@Autowired
	private JobcoinClient jobcoinClient;

	private static final long MAX_DEPOSIT_LENGTH_MILLIS = 1000 * 60 * 60;
	private static final long MAX_TRANSACTION_DELAY_MILLIS = 1000 * 60;
	private static final ScheduledExecutorService MIXING_TRANSACTION_SERVICE = Executors.newScheduledThreadPool(10);
	private static final SecureRandom RANDOM = new SecureRandom();
	private final ReentrantLock pendingDepositsLock = new ReentrantLock();

	// this would all be way less messy with a database
	private final AtomicReference<Set<PendingDeposit>> pendingDepositsReference = new AtomicReference<>(
			(Set<PendingDeposit>) new HashSet<PendingDeposit>());
	private final Set<ScheduledFuture<MixingTransactionResult>> pendingMixingTransactions = new HashSet<>();

	@Autowired
	public MixingService(FundsService fundsService) {
		this.fundsService = fundsService;
	}

	@Scheduled(initialDelay = 60000, fixedRate = 60000)
	public void checkAddressForDeposit() {
		Set<PendingDeposit> pendingDepositsCopy;
		try {
			pendingDepositsLock.lock();
			pendingDepositsCopy = pendingDepositsReference
					.getAndSet((Set<PendingDeposit>) new HashSet<PendingDeposit>());
		} finally {
			pendingDepositsLock.unlock();
		}
		Iterator<PendingDeposit> pendingDepositIter = pendingDepositsCopy.iterator();
		while (pendingDepositIter.hasNext()) {
			PendingDeposit pendingDeposit = pendingDepositIter.next();
			try {
				AddressInfo addressInfo = jobcoinClient.getAddressInfo(pendingDeposit.getDepositAddress());
				BigDecimal depositAmount = new BigDecimal(addressInfo.getBalance());
				Set<String> inputAddresses = new HashSet<>();
				if (depositAmount.compareTo(BigDecimal.ZERO) > 0) {
					UUID reservationId = fundsService.reserveFunds(depositAmount, inputAddresses,
							pendingDeposit.getOutputAddresses());
					ScheduledFuture<MixingTransactionResult> future = MIXING_TRANSACTION_SERVICE.schedule(
							getScheduledMixingTransaction(reservationId),
							RANDOM.nextLong() % MAX_TRANSACTION_DELAY_MILLIS, TimeUnit.MILLISECONDS);
					pendingMixingTransactions.add(future);
					pendingDepositIter.remove();
				} else if (pendingDeposit.getExpirationDateMillis() > System.currentTimeMillis()) {
					pendingDepositIter.remove();
				}
			} catch (JobcoinClientException e) {
				System.out.println("Error getting address info during deposit check for address ["
						+ pendingDeposit.getDepositAddress() + "]");
				e.printStackTrace();
			}
		}
		try {
			pendingDepositsLock.lock();
			Set<PendingDeposit> currentPendingDeposits = pendingDepositsReference.get();
			currentPendingDeposits.addAll(pendingDepositsCopy);
			pendingDepositsReference.set(currentPendingDeposits);
		} finally {
			pendingDepositsLock.unlock();
		}
	}

	public String generateDepositAddress(List<String> outputAddresses) {
		Set<String> outputAddressSet = new HashSet<>(outputAddresses);
		String depositAddress = new BigInteger(130, RANDOM).toString(32);
		PendingDeposit pendingDeposit = new PendingDeposit(depositAddress, outputAddressSet,
				System.currentTimeMillis() + MAX_DEPOSIT_LENGTH_MILLIS);
		try {
			pendingDepositsLock.lock();
			Set<PendingDeposit> pendingDeposits = pendingDepositsReference.get();
			pendingDeposits.add(pendingDeposit);
			pendingDepositsReference.set(pendingDeposits);
		} finally {
			pendingDepositsLock.unlock();
		}

		return depositAddress;
	}

	public Callable<MixingTransactionResult> getScheduledMixingTransaction(final UUID reservationId) {
		return new Callable<MixingTransactionResult>() {

			public MixingTransactionResult call() throws Exception {
				boolean success = fundsService.sendFunds(reservationId);
				return new MixingTransactionResult(reservationId, success);
			}

		};
	}

	// funds
	// amount - originating_address_hash - email_hash
	// scheduled delivery
	// amount - address - delivery_date_time - originating_address_hash
	//

}
