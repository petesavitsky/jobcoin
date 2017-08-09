package com.petersavitsky.jobcoinmixer.funds;

public class InsufficientFundsException extends Exception {

	private static final long serialVersionUID = 7600491990293179532L;

	public InsufficientFundsException(String msg) {
		super(msg);
	}
	
}
