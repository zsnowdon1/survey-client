package com.voting.survey_client.dao;

import com.voting.survey_client.entity.Vote;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyDao {

    void sendVote(Vote vote);
}
