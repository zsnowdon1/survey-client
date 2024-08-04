package com.voting.survey_client.dto;

public class SendVoteRequest {

    private int choiceId;

    private String username;

    public SendVoteRequest(int choiceId, String username) {
        this.choiceId = choiceId;
        this.username = username;
    }

    public int getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(int choiceId) {
        this.choiceId = choiceId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
