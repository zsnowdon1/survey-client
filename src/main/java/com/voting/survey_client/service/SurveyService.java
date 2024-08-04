package com.voting.survey_client.service;

import com.voting.survey_client.dto.SendVoteRequest;
import org.springframework.stereotype.Service;

@Service
public interface SurveyService {

    void sendVote(SendVoteRequest request);
}
