package com.voting.survey_client.dao;

import com.voting.survey_client.entity.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SurveyDaoImpl implements SurveyDao{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void sendVote(Vote vote) {

    }
}
