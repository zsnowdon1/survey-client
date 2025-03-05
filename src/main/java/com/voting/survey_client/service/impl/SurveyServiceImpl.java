package com.voting.survey_client.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voting.entities.SurveyDTO;
import com.voting.mongoData.Survey;
import com.voting.survey_client.dto.SurveyRequest;
import com.voting.survey_client.entity.SelectedChoice;
import com.voting.survey_client.service.SurveyService;
import com.voting.utils.SurveyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Primary
public class SurveyServiceImpl implements SurveyService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ChannelTopic channelTopic;

    private static final String SURVEY_CACHE_PREFIX = "Survey:";

    private static final Logger logger = LoggerFactory.getLogger(SurveyServiceImpl.class);

    public SurveyServiceImpl(RedisTemplate<String, Object> redisTemplate, ChannelTopic channelTopic) {
        this.redisTemplate = redisTemplate;
        this.channelTopic = channelTopic;
    }

    @Override
    public SurveyDTO getSurvey(String accessCode) {
//        String cacheKey = SURVEY_CACHE_PREFIX + id;
//        Survey cachedSurvey = (Survey) redisTemplate.opsForValue().get(cacheKey);
//        if(cachedSurvey != null) {
//            if(cachedSurvey.getStatus().equals("LIVE") && cachedSurvey.getAccessCode().equals(accessCode)) {
//                return SurveyMapper.toDTOSurvey(cachedSurvey);
//            } else {
//                throw new RuntimeException("Survey is not live for id: " + id);
//            }
//        } else {
//            throw new RuntimeException("Survey is not found for id: " + id);
//        }
        Survey cachedSurvey = (Survey) redisTemplate.opsForValue().get(SURVEY_CACHE_PREFIX + accessCode);
        return SurveyMapper.toDTOSurvey(cachedSurvey);
    }

    @Override
    public void postVote(SurveyRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        for(SelectedChoice choice: request.getResponses()) {
            logger.info("Processing vote for question " + choice.getQuestionId());
            String questionId = choice.getQuestionId();
            String choiceId = choice.getChoiceId();

            String redisKey = "survey:" + request.getSurveyId() + ":question:" + questionId + ":results";
            logger.info("Incrementing votes for choice:" + choiceId);
            long newCount = redisTemplate.opsForHash().increment(redisKey, choiceId, 1);

            HashMap<String, Object> voteData = new HashMap<>();
            voteData.put("choiceId", choice.getChoiceId());
            voteData.put("questionId", choice.getQuestionId());
            voteData.put("votes", newCount);

            try {
                String updateMessage = objectMapper.writeValueAsString(voteData);
                redisTemplate.convertAndSend(channelTopic.getTopic(), updateMessage);
            } catch (Exception e) {
                logger.error("Failed to public vote count update to redis");
            }
            logger.info("Processed vote for survey: " + request.getSurveyId() + ", question: " + questionId + ", choice: " + choiceId);
        }
    }

}
