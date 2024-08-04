package com.voting.survey_client.controller;

import com.voting.survey_client.dto.SendVoteRequest;
import com.voting.survey_client.service.SurveyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vote")
public class SurveyController {

    @Autowired
    private SurveyServiceImpl surveyService;

    @PostMapping("/send")
    public ResponseEntity<String> sendVote(@RequestBody SendVoteRequest request) {

        try {
            surveyService.sendVote(request);
        } catch (Exception e) {

        }
        return null;
    }


}
