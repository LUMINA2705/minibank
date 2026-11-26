package com.minibank.account_service.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

@Component
public class TransactionIdGenerator {
		
	// Singleton bean shared by all request threads → AtomicLong for thread safety.
    // Limitation: per-JVM and resets on restart → random suffix avoids duplicates.
    // Production: use a DB sequence (cluster-wide unique).	private final AtomicLong counter=new AtomicLong();
    private final AtomicLong counter = new AtomicLong();

	public String next() {
		String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String sequence = String.format("%06d", counter.incrementAndGet());
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "TXN" + date + sequence + "-" + random;
	}

}
