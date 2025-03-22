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
import java.util.UUID;

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
        Survey cachedSurvey = (Survey) redisTemplate.opsForValue().get(SURVEY_CACHE_PREFIX + accessCode);
        return SurveyMapper.toDTOSurvey(cachedSurvey);
    }

    @Override
    public void postVote(SurveyRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        String correlationId = UUID.randomUUID().toString();

        for (SelectedChoice choice : request.getResponses()) {
            String questionId = choice.getQuestionId();
            String choiceId = choice.getChoiceId();
            String redisKey = "survey:" + request.getSurveyId() + ":question:" + questionId + ":results";

            logger.info("CorrelationID: {} - Processing vote for question: {}", correlationId, questionId);
            long newCount = redisTemplate.opsForHash().increment(redisKey, choiceId, 1);

            HashMap<String, Object> voteData = new HashMap<>();
            voteData.put("choiceId", choiceId);
            voteData.put("questionId", questionId);
            voteData.put("votes", newCount);
            voteData.put("correlationId", correlationId);

            try {
                String updateMessage = objectMapper.writeValueAsString(voteData);
                redisTemplate.convertAndSend(channelTopic.getTopic(), updateMessage);
                logger.info("CorrelationID: {} - Published vote update for choice: {}", correlationId, choiceId);
            } catch (Exception e) {
                logger.error("CorrelationID: {} - Failed to publish vote update: {}", correlationId, e.getMessage());
            }
        }
        logger.info("CorrelationID: {} - Processed vote for survey: {}", correlationId, request.getSurveyId());
    }

}
