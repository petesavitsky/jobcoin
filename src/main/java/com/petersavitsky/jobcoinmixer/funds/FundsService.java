package com.petersavitsky.jobcoinmixer.funds;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.petersavitsky.jobcoinmixer.client.AddressInfo;
import com.petersavitsky.jobcoinmixer.client.JobcoinClient;
import com.petersavitsky.jobcoinmixer.client.JobcoinClient.SendCoinsResult;
import com.petersavitsky.jobcoinmixer.client.JobcoinClientException;
import com.petersavitsky.jobcoinmixer.client.Transaction;

import groovy.lang.Singleton;

@Service
@Singleton
public class FundsService {

	private static final SecureRandom RANDOM = new SecureRandom();
	private final Map<UUID, FundsReservation> reservedFunds = new HashMap<>();
	private Map<String, AddressBalance> availableFunds = new HashMap<>();

	@Autowired
	private JobcoinClient jobcoinClient;

	@PostConstruct
	public synchronized void init() {
		String addressBase = "mixer-house-account";
		for (int i = 1; i <= 5; i++) {
			try {
				String address = addressBase + i;
				AddressInfo addressInfo = jobcoinClient.getAddressInfo(address);
				BigDecimal balance = new BigDecimal(addressInfo.getBalance());
				if (BigDecimal.ZERO.compareTo(balance) < 1) {
					Set<String> originAddresses = new HashSet<>();
					for (Transaction transaction : addressInfo.getTransactions()) {
						if (transaction.getToAddress().equals(address) && transaction.getFromAddress() != null) {
							originAddresses.add(transaction.getFromAddress());
						}
					}
					AddressBalance addressBalance = new AddressBalance(address, originAddresses, balance);
					availableFunds.put(address, addressBalance);
				}
			} catch (JobcoinClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public synchronized UUID reserveFunds(BigDecimal amount, Set<String> blacklistedAddresses,
			Set<String> outputAddresses) {
		UUID fundsReservationId = UUID.randomUUID();
		while (reservedFunds.containsKey(fundsReservationId)) {
			fundsReservationId = UUID.randomUUID();
		}
		Set<FundsReservationAddress> fundsReservationAddresses = findAddressesForFunds(amount, outputAddresses,
				blacklistedAddresses);
		for (FundsReservationAddress fundsReservationAddress : fundsReservationAddresses) {
			AddressBalance availableBalance = availableFunds.get(fundsReservationAddress.getFromAddress());
			try {
				availableBalance.removeFunds(fundsReservationAddress.getReservedAmount());
			} catch (InsufficientFundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (availableBalance.getBalance().compareTo(BigDecimal.ZERO) > 0) {
				availableFunds.put(availableBalance.getAddress(), availableBalance);
			} else {
				availableFunds.remove(availableBalance.getAddress());
			}
		}
		FundsReservation fundsReservation = new FundsReservation(fundsReservationId, fundsReservationAddresses);
		reservedFunds.put(fundsReservationId, fundsReservation);
		return fundsReservationId;
	}

	public synchronized void addFunds(String address, String originAddress, BigDecimal amount) {
		AddressBalance addressBalance = availableFunds.get(address);
		if (addressBalance == null) {
			Set<String> originAddresses = new HashSet<>();
			originAddresses.add(originAddress);
			addressBalance = new AddressBalance(address, originAddresses, amount);
		} else {
			addressBalance.addFunds(originAddress, amount);
		}
		availableFunds.put(address, addressBalance);
	}

	public synchronized boolean sendFunds(UUID reservationId) {
		FundsReservation fundsReservation = reservedFunds.remove(reservationId);
		if (fundsReservation == null) {
			System.out.println("Attempted to send funds for non-existent reservation id [" + reservationId + "]");
			return false;
		}
		Set<FundsReservationAddress> outputs = fundsReservation.getReservedAddresses();
		boolean success = true;
		for (FundsReservationAddress output : outputs) {
			SendCoinsResult result = jobcoinClient.sendCoins(output.getReservedAmount(), output.getFromAddress(),
					output.getToAddress());
			success &= result.equals(SendCoinsResult.SUCCESS);
			// TODO: log errors
		}
		return success;
	}

	public synchronized boolean cancelReservation() {
		// TODO: remove reservation and make funds available again
		return false;
	}

	private Set<FundsReservationAddress> findAddressesForFunds(BigDecimal amount, Set<String> outputAddresses,
			Set<String> blacklistedAddresses) {
		// this might break down if we don't have enough addresses for the
		// number of outputs - either put a limit, or allow more than one output
		// per available source
		BigDecimal amountRemaining = amount;
		LinkedList<Pair<String, BigDecimal>> disbursementAmounts = new LinkedList<>();
		for (String outputAddress : outputAddresses) {
			BigDecimal disbursementAmount = null;
			if (disbursementAmounts.size() == outputAddresses.size() - 1) {
				disbursementAmount = amountRemaining;
			} else {
				disbursementAmount = getRandomDoubleInRange(BigDecimal.ZERO, amountRemaining);
			}
			amountRemaining = amountRemaining.subtract(disbursementAmount);
			disbursementAmounts.add(Pair.of(outputAddress, disbursementAmount));
			if (amountRemaining.compareTo(BigDecimal.ZERO) < 1) {
				break;
			}
		}
		Set<FundsReservationAddress> reservations = new HashSet<>();
		for (AddressBalance addressBalance : availableFunds.values()) {
			Set<String> blacklistedOriginAddresses = addressBalance.getOriginAddresses();
			blacklistedOriginAddresses.retainAll(blacklistedAddresses);
			if (blacklistedOriginAddresses.isEmpty()) {
				// good to go
				Pair<String, BigDecimal> addressDisbursmentPair = disbursementAmounts.peekFirst();
				BigDecimal requiredAmount = addressDisbursmentPair.getRight();
				if (requiredAmount.compareTo(addressBalance.getBalance()) < 1) {
					// we have enough
					FundsReservationAddress reservationAmount = new FundsReservationAddress(addressBalance.getAddress(),
							addressDisbursmentPair.getLeft(), requiredAmount);
					reservations.add(reservationAmount);
					disbursementAmounts.removeFirst();
					if (disbursementAmounts.isEmpty()) {
						break;
					}
				}
			}
		}
		return reservations;
	}

	private BigDecimal getRandomDoubleInRange(BigDecimal rangeStart, BigDecimal rangeEnd) {
		BigDecimal randomDouble = new BigDecimal(RANDOM.nextDouble());
		randomDouble = randomDouble.setScale(8, RoundingMode.HALF_UP);
		BigDecimal randomDoubleInRange = rangeEnd.subtract(rangeStart);
		randomDoubleInRange = randomDouble.multiply(randomDoubleInRange);
		randomDoubleInRange = randomDoubleInRange.add(rangeStart);
		return randomDoubleInRange;
	}
}
