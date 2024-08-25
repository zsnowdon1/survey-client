package com.voting.survey_client.service;

import com.voting.survey_client.dto.SurveyRequest;
import org.springframework.stereotype.Service;

@Service
public interface KafkaProducerService {

    void sendVote(SurveyRequest vote);

}
