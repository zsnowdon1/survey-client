package com.voting.survey_client.controller;

import com.voting.survey_client.dto.SurveyRequest;
import com.voting.survey_client.service.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vote")
@CrossOrigin(origins = "http:/localhost:3000")
public class SurveyController {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    private static final Logger logger = LoggerFactory.getLogger(SurveyController.class);

    @PostMapping("/submitSurvey")
    public ResponseEntity<String> sendVote(@RequestBody SurveyRequest request) {
        logger.info("RECEIVED SEND VOTE REQUEST {}", request.toString());
        try {
            kafkaProducerService.sendVote(request);
            return new ResponseEntity<>("Successfully submitted votes", HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
