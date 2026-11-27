package com.minibank.transaction_service.messaging;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Table(name = "outbox_events")
@Entity
public class OutboxEvent {

	@Id
	private UUID id;
	
	@Column(name= "topic",nullable = false,updatable = false)
	private String topic;
	
	@Column(name = "message_key", nullable = false,updatable = false)
	private String messageKey;
	
	@Column(nullable = false, updatable = false, columnDefinition = "text")
	private String payload;
	
	@Column(name="created_at", updatable = false,nullable = false)
	private Instant createdAt;
	
	@Column(name="sent_at" )
	private Instant sentAt;
	
	
	protected  OutboxEvent() {
	}

	@PrePersist
	 void onCreate() {
		 
 		this.createdAt=Instant.now();
	}
	
	public static OutboxEvent of(String topic, String messageKey, String payload) {
		 OutboxEvent outboxEvent= new OutboxEvent();
		 
		 outboxEvent.id=UUID.randomUUID();
		 outboxEvent.topic=topic;
		 outboxEvent.payload=payload;
		 outboxEvent.messageKey=messageKey;
		 
		 return outboxEvent;
	}

	public void markSent() {
	    this.sentAt = Instant.now();
	}
	
	public UUID getId() {
		return id;
	}

	public String getTopic() {
		return topic;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public String getPayload() {
		return payload;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getSentAt() {
		return sentAt;
	}

		
}
