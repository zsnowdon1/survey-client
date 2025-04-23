package com.voting.survey_client.service.impl;

import com.voting.survey_client.entity.SurveyRequest;
import com.voting.survey_client.service.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerServiceImpl.class);

    public KafkaProducerServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendVote(SurveyRequest vote) {
        kafkaTemplate.send("votes-topic", vote);
    }
}
