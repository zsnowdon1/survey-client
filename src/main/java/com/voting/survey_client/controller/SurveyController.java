package com.voting.survey_client.controller;

import com.voting.entities.SurveyDTO;
import com.voting.survey_client.dto.SurveyRequest;
import com.voting.survey_client.service.KafkaProducerService;
import com.voting.survey_client.service.SurveyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/surveys")
@CrossOrigin
public class SurveyController {

    private final KafkaProducerService kafkaProducerService;

    private final SurveyService surveyService;

    private static final Logger logger = LoggerFactory.getLogger(SurveyController.class);

    public SurveyController(KafkaProducerService kafkaProducerService, SurveyService surveyService) {
        this.kafkaProducerService = kafkaProducerService;
        this.surveyService = surveyService;
    }

    @GetMapping()
    public ResponseEntity<SurveyDTO> getSurvey(@RequestParam String accessCode) {
        try {
            SurveyDTO survey = surveyService.getSurvey(accessCode);
            return new ResponseEntity<>(survey, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/submitSurvey")
    public ResponseEntity<String> submitSurvey(@RequestBody SurveyRequest request) {
        logger.info("RECEIVED SEND VOTE REQUEST {}", request.toString());
        try {
            surveyService.postVote(request);
            // When kafka goes live...
            kafkaProducerService.sendVote(request);
            return new ResponseEntity<>("Successfully submitted votes", HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
