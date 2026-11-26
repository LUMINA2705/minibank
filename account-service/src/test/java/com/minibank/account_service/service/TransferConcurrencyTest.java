package com.minibank.account_service.service;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.minibank.account_service.domain.AccountType;
import com.minibank.account_service.dto.AccountResponse;
import com.minibank.account_service.dto.CreateAccountRequest;
import com.minibank.account_service.dto.TransferRequest;

@SpringBootTest
public class TransferConcurrencyTest {

	@Autowired
	AccountService accountService;
	
	@Autowired
	TransferService transferService;
	
	 @Test
	    void hundredParallelTransfers_moveExactlyTheRightAmount() throws Exception {
		// ---------- Arrange: two fresh accounts ----------
	        String customer = "CONC-" + UUID.randomUUID();
	        AccountResponse a = accountService.open(
	                new CreateAccountRequest(customer, AccountType.SAVINGS, new BigDecimal("10000.00")));
	        AccountResponse b = accountService.open(
	                new CreateAccountRequest(customer, AccountType.SAVINGS, BigDecimal.ZERO));

	        int threads=100;
	        ExecutorService pool=Executors.newFixedThreadPool(20);
	        CountDownLatch startGun=new CountDownLatch(1);
	        CountDownLatch finished=new CountDownLatch(threads);
	        AtomicInteger success=new AtomicInteger();
	        AtomicInteger failed=new AtomicInteger();
	        
	        // ---------- Act: 100 transfers, released together ----------
	        for(int i=0;i<threads;i++) {
	        	pool.submit(()->{
	        	try {
	        		startGun.await();
	        		transferService.transfer(UUID.randomUUID().toString(),
	        				new TransferRequest(a.id(), b.id(), new BigDecimal("100.00")));
	        		success.incrementAndGet();
	        	}catch(Exception e) {
	        		failed.incrementAndGet();
	        		
	        		System.out.println("Transfer failed: "+e.getClass().getSimpleName());
	        	}finally {
	        		finished.countDown();
	        	}
	        });
	        }
	      startGun.countDown();
	      finished.await(60,TimeUnit.SECONDS);
	      pool.shutdown();
	      
	      //Assert
	        assertThat(success.get()).isEqualTo(100);
	        assertThat(failed.get()).isZero();
	        assertThat(accountService.get(a.id()).balance()).isEqualByComparingTo("0.00");
	        assertThat(accountService.get(b.id()).balance()).isEqualByComparingTo("10000.00");

	         
	 }
}
