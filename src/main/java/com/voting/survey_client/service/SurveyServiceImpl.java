package com.voting.survey_client.service;

import com.voting.survey_client.dao.SurveyDaoImpl;
import com.voting.survey_client.dto.SendVoteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SurveyServiceImpl implements SurveyService {

    @Autowired
    private SurveyDaoImpl surveyDao;

    @Override
    public void sendVote(SendVoteRequest request) {

    }
}
