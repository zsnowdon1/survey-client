package com.voting.survey_client.service;

import com.voting.survey_client.entity.Vote;
import org.springframework.stereotype.Service;

@Service
public interface KafkaProducerService {

    void sendVote(String key, Object message);

}
