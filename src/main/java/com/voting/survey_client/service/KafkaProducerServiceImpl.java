package com.voting.survey_client.service;

import com.voting.survey_client.entity.Vote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerServiceImpl.class);

//    @Autowired
//    public KafkaProducerServiceImpl(KafkaTemplate<String, Object> kafkaTemplate,
//                                @Value("${spring.kafka.topic.name}") String topicName) {
//        this.kafkaTemplate = kafkaTemplate;
//        this.topicName = topicName;
//    }

    @Autowired
    public KafkaProducerServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendVote(String key, Object message) {
        logger.info("SENDING KAFKA MESSAGE FROM PRODUCER");
        kafkaTemplate.send("votes-topic", key, message);
    }
}
