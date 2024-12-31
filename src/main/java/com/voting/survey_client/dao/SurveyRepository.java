package com.voting.survey_client.dao;

import com.voting.survey_client.entity.Survey;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SurveyRepository extends MongoRepository<Survey, String> {
}
