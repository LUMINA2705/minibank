package com.minibank.transaction_service.messaging;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.minibank.transaction_service.OutBoxRepository;

@Component
public class OutboxRelay {

	private static final Logger log=LoggerFactory.getLogger(OutboxRelay.class);
	private final OutBoxRepository outboxRepository;
	private KafkaTemplate<String, String> kafkaTemplate;
	
	 public OutboxRelay(OutBoxRepository outboxRepository,
             KafkaTemplate<String, String> kafkaTemplate) {
		 	this.outboxRepository = outboxRepository;
		 	this.kafkaTemplate = kafkaTemplate;
	 	}
	 
	 @Scheduled(fixedDelay = 1000)
	    @Transactional
	    public void publishPending() {
	        List<OutboxEvent> pending = outboxRepository.findTop100BySentAtIsNullOrderByCreatedAtAsc();

	        for (OutboxEvent event : pending) {
	            try {
	                kafkaTemplate.send(event.getTopic(), event.getMessageKey(), event.getPayload())
	                             .join();          // wait until Kafka confirms
	                event.markSent();              // saved at commit (dirty checking)
	            } catch (RuntimeException ex) {
	                log.warn("Kafka send failed for outbox event {}; will retry", event.getId(), ex);
	                break;                         // keep order: don't skip ahead
	            }
	        }
	    }
}
