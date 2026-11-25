package com.minibank.account_service.exception;

public class BusinessRuleException extends RuntimeException {

	private final String code ;//"BALANCE_NOT_ZERO"

	public BusinessRuleException(String code, String message) {
		super(message);
		this.code=code;
	}
	
	public String getCode( ) {
		return code;
	}
	
	
}
