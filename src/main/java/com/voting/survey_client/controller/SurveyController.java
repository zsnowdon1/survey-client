package com.voting.survey_client.controller.impl;

import com.voting.entities.SurveyDTO;
import com.voting.survey_client.entity.SurveyRequest;
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

    private final SurveyService surveyService;

    private static final Logger logger = LoggerFactory.getLogger(SurveyController.class);

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    /**
     * Retrieves a live survey for the clients who are filling out the survey
     * @param accessCode Live code that is unique for each survey when the host makes it live
     * @return SurveyDto Object that contains all details for a survey
     */
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

    /**
     * Endpoint that is called after a client fills out the survey and submits it
     * @param request Contains the filled out survey details
     * @return String TODO: Update response
     */
    @PostMapping("/submitSurvey")
    public ResponseEntity<String> submitSurvey(@RequestBody SurveyRequest request) {
        logger.info("RECEIVED SEND VOTE REQUEST {}", request.toString());
        try {
            surveyService.postVote(request);
            // When kafka goes live...
//            kafkaProducerService.sendVote(request);
            return new ResponseEntity<>("Successfully submitted votes", HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
