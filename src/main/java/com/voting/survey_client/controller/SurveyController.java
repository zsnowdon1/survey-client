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
public interface SurveyController {

    @GetMapping()
    public ResponseEntity<SurveyDTO> getSurvey(@RequestParam String accessCode);

    @PostMapping("/submitSurvey")
    public ResponseEntity<String> submitSurvey(@RequestBody SurveyRequest request);

}
