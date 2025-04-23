package com.voting.survey_client.service;

import com.voting.entities.SurveyDTO;
import com.voting.survey_client.entity.SurveyRequest;
import org.springframework.stereotype.Service;

@Service
public interface SurveyService {

    SurveyDTO getSurvey(String accessCode);

    void postVote(SurveyRequest request);
}
