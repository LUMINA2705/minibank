package com.minibank.account_service.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.minibank.account_service.domain.Account;


@Repository
public interface AccountRepository extends JpaRepository<Account, UUID>{

	Page<Account> findByCustomerId(String customerId, Pageable pageable);
	
	@Query(value="select nextval('account_number_seq')",nativeQuery=true)
		long nextAccountNumber();
}
