package com.minibank.account_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.minibank.account_service.dto.AccountResponse;
import com.minibank.account_service.dto.CreateAccountRequest;
import com.minibank.account_service.dto.UpdateStatusRequest;
import com.minibank.account_service.service.AccountService;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountsController {

	private final AccountService accountService;

	public AccountsController(AccountService accountService) {
		this.accountService = accountService;
	}
	
	

	@PostMapping
	public ResponseEntity<AccountResponse> open(@Valid @RequestBody CreateAccountRequest accountRequest) {

		AccountResponse opened = accountService.open(accountRequest);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(opened.id())
				.toUri();
		return ResponseEntity.created(location).body(opened);
	}
	
	@GetMapping("/{id}")
	public AccountResponse get(@PathVariable UUID id) {
	    return accountService.get(id);
	}
	
	@GetMapping
	public Page<AccountResponse> list(
			@RequestParam (required=false) String CustomerId,
			@PageableDefault(size=20,sort="CreatedAt", direction=Sort.Direction.DESC) Pageable pageable){
		
		Page<AccountResponse> list = accountService.list(CustomerId, pageable);
		return list;
	}
	
	@PatchMapping("/{id}/status")
	public AccountResponse changeStatus(@PathVariable UUID id,
										@Valid @RequestBody UpdateStatusRequest req) {
		AccountResponse statusChanged = accountService.changeStatus(id, req.status());
		return statusChanged;
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> close(@PathVariable UUID id){
		accountService.close(id);
		return ResponseEntity.noContent().build();
	}
	
	
	

}
