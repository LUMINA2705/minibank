package com.minibank.account_service.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException{

	public AccountNotFoundException(UUID id ) {
		
		
		super("Account not found: " + id);
	}
	
	

}
