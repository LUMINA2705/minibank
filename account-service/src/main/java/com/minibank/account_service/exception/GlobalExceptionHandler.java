package com.minibank.account_service.exception;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{
	
    private static final String TYPE_BASE = "https://minibank.dev/errors/";

	
    // 404: GET/PATCH/DELETE with an id that doesn't exist
	@ExceptionHandler(AccountNotFoundException.class)
	ProblemDetail handleNotFound(AccountNotFoundException ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
		pd.setTitle("Account not found");
		pd.setType(URI.create(TYPE_BASE+ "account-not-found"));
		return pd;
		}
	
	 // 422: valid request, but it breaks a banking rule
		@ExceptionHandler(BusinessRuleException.class)
		ProblemDetail handleBusinessRule(BusinessRuleException ex) {
			ProblemDetail pd=	ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422), ex.getMessage());
		pd.setTitle("Business rule violated");
		pd.setType(URI.create(TYPE_BASE+ex.getCode().toLowerCase().replace("_","-")));
		pd.setProperty("code", ex.getCode());
		
		return pd;
		}
		
	  // 409: two requests changed the same account at the same time (@Version)
		@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
		ProblemDetail handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
			ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, 
					"The account was changed by another request. Please retry.");
			pd.setTitle("Concurrent update");
			pd.setType(URI.create(TYPE_BASE+"concurrent-update"));
			return pd;
		}
		
		 // 400: @Valid failed → list every bad field
	    @Override
	    protected ResponseEntity<Object> handleMethodArgumentNotValid(
	            MethodArgumentNotValidException ex, HttpHeaders headers,
	            HttpStatusCode status, WebRequest request) {

	        Map<String, String> errors = new LinkedHashMap<>();
	        ex.getBindingResult().getFieldErrors()
	                .forEach(fe -> errors.putIfAbsent(fe.getField(), fe.getDefaultMessage()));

	        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
	        pd.setTitle("Validation error");
	        pd.setType(URI.create(TYPE_BASE + "validation"));
	        pd.setProperty("errors", errors);
	        return ResponseEntity.badRequest().body(pd);
	    }
	    
	    @ExceptionHandler(DataIntegrityViolationException.class)
	    ProblemDetail handleDuplicate(DataIntegrityViolationException ex) {
	        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
	                "This request conflicts with an existing record (possibly a duplicate). Please retry.");
	        pd.setTitle("Conflict");
	        pd.setType(URI.create(TYPE_BASE + "conflict"));
	        return pd;
	    }
		

		
		

		
	

}
