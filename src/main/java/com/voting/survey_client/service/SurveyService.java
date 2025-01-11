package com.voting.survey_client.service;

import com.voting.entities.SurveyDTO;
import org.springframework.stereotype.Service;

@Service
public interface SurveyService {

    SurveyDTO getSurvey(String id);
}
