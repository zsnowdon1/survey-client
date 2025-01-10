package com.voting.survey_client.dao;

import com.voting.survey_client.mongoData.Survey;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.MongoRepository;

@Primary
public interface SurveyRepository extends MongoRepository<Survey, String> {
}
