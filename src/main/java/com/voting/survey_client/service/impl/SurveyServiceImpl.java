package com.voting.survey_client.service.impl;

import com.voting.entities.SurveyDTO;
import com.voting.mongoData.Survey;
import com.voting.survey_client.dto.SurveyRequest;
import com.voting.survey_client.entity.SelectedChoice;
import com.voting.survey_client.service.SurveyService;
import com.voting.utils.SurveyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

@Service
@Primary
public class SurveyServiceImpl implements SurveyService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final DefaultRedisScript<Object> redisScript;
    private static final String SURVEY_CACHE_PREFIX = "survey:cache:";
    private static final String REDIS_SURVEY_RESULT_PREFIX = "survey:hosts:";
    private static final Logger logger = LoggerFactory.getLogger(SurveyServiceImpl.class);

    public SurveyServiceImpl(@Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate,
                             DefaultRedisScript<Object> redisScript) {
        this.redisTemplate = redisTemplate;
        this.redisScript = redisScript;
    }

    @Override
    public SurveyDTO getSurvey(String accessCode) {
        Survey cachedSurvey = (Survey) redisTemplate.opsForValue().get(SURVEY_CACHE_PREFIX + accessCode);
        if (cachedSurvey == null) {
            throw new RuntimeException("Survey not found for access code: " + accessCode);
        }
        if (!cachedSurvey.getStatus().equals("LIVE")) {
            throw new RuntimeException("Survey is not live for access code: " + accessCode);
        }
        return SurveyMapper.mapToDTO(cachedSurvey);
    }

    /**
     * Checks if a host is currently viewing live results, publish to redis if they are. Probably going to change to lua script
     * @param request
     */
    @Override
    public void postVote(SurveyRequest request) {
        String surveyId = request.getSurveyId();
        String hostsKey = REDIS_SURVEY_RESULT_PREFIX + surveyId;
        String correlationId = UUID.randomUUID().toString();

        for (SelectedChoice choice : request.getResponses()) {
            String questionId = choice.getQuestionId();
            String choiceId = choice.getChoiceId();
            String redisKey = "survey:" + surveyId + ":question:" + questionId + ":results";
            String channel = "survey:" + surveyId + ":results";

            logger.info("CorrelationID: {} - Processing vote for question: {}", correlationId, questionId);

            try {
                Object newCount = redisTemplate.execute(
                        redisScript,
                        Arrays.asList(redisKey, channel, hostsKey),
                        choiceId, questionId
                );
                logger.info("CorrelationID: {} - Vote processed for choice: {}, new count: {}",
                        correlationId, choiceId, newCount);
            } catch (Exception e) {
                logger.error("CorrelationID: {} - Failed to process vote for choice: {}", correlationId, choiceId, e);
            }
        }
        logger.info("CorrelationID: {} - Processed vote for survey: {}", correlationId, surveyId);
    }

}
