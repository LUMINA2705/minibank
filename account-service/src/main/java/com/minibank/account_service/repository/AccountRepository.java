package com.minibank.account_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.minibank.account_service.domain.Account;

import jakarta.persistence.LockModeType;


@Repository
public interface AccountRepository extends JpaRepository<Account, UUID>{

	Page<Account> findByCustomerId(String customerId, Pageable pageable);
	
	@Query(value="select nextval('account_number_seq')",nativeQuery=true)
		long nextAccountNumber();
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select a from Account a where a.id = :id")
	Optional<Account> findByIdForUpdate(@Param("id") UUID id);
}
