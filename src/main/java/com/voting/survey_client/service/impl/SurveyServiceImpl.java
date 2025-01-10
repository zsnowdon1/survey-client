package com.voting.survey_client.service.impl;

import com.voting.survey_client.dao.SurveyRepository;
import com.voting.survey_client.entity.SurveyDTO;
import com.voting.survey_client.mongoData.Survey;
import com.voting.survey_client.mongoData.SurveyMapper;
import com.voting.survey_client.service.SurveyService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Primary
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository surveyRepository;

    public SurveyServiceImpl(SurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    @Override
    public SurveyDTO getSurvey(String id) {
        Optional<Survey> mongoSurvey = this.surveyRepository.findById(id);
        return SurveyMapper.toDTOSurvey(mongoSurvey.orElseThrow(() -> new RuntimeException("Survey not found with id: " + id)));
    }
}
