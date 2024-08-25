package com.voting.survey_client.dto;

import com.voting.survey_client.entity.SelectedChoice;

import java.util.Arrays;

public class SurveyRequest {

    private String username;

    private long surveyId;

    private SelectedChoice[] responses;

    public SurveyRequest(String username, long surveyId, SelectedChoice[] responses) {
        this.username = username;
        this.surveyId = surveyId;
        this.responses = responses;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(long surveyId) {
        this.surveyId = surveyId;
    }

    public SelectedChoice[] getResponses() {
        return responses;
    }

    public void setResponses(SelectedChoice[] responses) {
        this.responses = responses;
    }

    @Override
    public String toString() {
        return "SurveyRequest{" +
                "username='" + username + '\'' +
                ", surveyId=" + surveyId +
                ", responses=" + Arrays.toString(responses) +
                '}';
    }
}
