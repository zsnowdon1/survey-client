package com.voting.survey_client.surveyTemplates;

import com.voting.mongoData.Choice;

import java.util.List;

public class QuestionTemplateDTO {

    private String questionId;

    private String questionText;

    private List<Choice> choiceList;
}
