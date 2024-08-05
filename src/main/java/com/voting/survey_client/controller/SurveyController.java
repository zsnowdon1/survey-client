package com.voting.survey_client.controller;

import com.voting.survey_client.dto.SendVoteRequest;
import com.voting.survey_client.entity.Vote;
import com.voting.survey_client.service.KafkaProducerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vote")
public class SurveyController {

    @Autowired
    private KafkaProducerServiceImpl kafkaProducerService;

    private static final Logger logger = LoggerFactory.getLogger(SurveyController.class);

    @PostMapping("/send")
    public ResponseEntity<String> sendVote(@RequestBody SendVoteRequest request) {
        logger.info("RECEIVED SEND VOTE REQUEST");
        try {
//            kafkaProducerService.sendVote("TEST_KEY", "TEST_VALUE");
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testResponse() {
        logger.info("RECEIVED TEST SURVEY CLIENT REQUEST");
        Vote vote = new Vote(5,4,"zsnowdon");
        kafkaProducerService.sendVote("TEST_KEY", vote);
        return new ResponseEntity<>("Success", HttpStatus.ACCEPTED);
    }


}
