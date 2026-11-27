package com.minibank.transaction_service.messaging;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.minibank.transaction_service.OutBoxRepository;

import tools.jackson.databind.json.JsonMapper;

@Component
public class OutboxWriter {
	private final OutBoxRepository outBoxRepository;
	private final JsonMapper jsonMapper;
	
	public OutboxWriter(OutBoxRepository outBoxRepository, JsonMapper jsonMapper) {
		this.outBoxRepository = outBoxRepository;
		this.jsonMapper = jsonMapper;
	}
	
	@Transactional(propagation = Propagation.MANDATORY)
	public void enqueue(String topic,String key,Object event) {
		String payload=jsonMapper.writeValueAsString(event);
		outBoxRepository.save(OutboxEvent.of(topic, key, payload));
		
	}
	
	

}
