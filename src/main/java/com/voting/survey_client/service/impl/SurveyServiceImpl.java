package com.voting.survey_client.service.impl;

import com.voting.entities.SurveyDTO;
import com.voting.entities.VoteUpdate;
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
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@Primary
public class SurveyServiceImpl implements SurveyService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, Object> messageRedisTemplate;

    private static final String SURVEY_CACHE_PREFIX = "Survey:";

    private static final Logger logger = LoggerFactory.getLogger(SurveyServiceImpl.class);

    public SurveyServiceImpl(@Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate,
                             @Qualifier("messageRedisTemplate") RedisTemplate<String, Object> messageRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.messageRedisTemplate = messageRedisTemplate;
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
        String activeHostsKey = "survey:" + request.getSurveyId() + ":active-hosts";
        Long activeHostsCount = redisTemplate.opsForSet().size(activeHostsKey);

        if (activeHostsCount != null && activeHostsCount > 0) {
            String correlationId = UUID.randomUUID().toString();
            String channel = "survey:" + request.getSurveyId() + ":results";

            for (SelectedChoice choice : request.getResponses()) {
                String questionId = choice.getQuestionId();
                String choiceId = choice.getChoiceId();
                String redisKey = "survey:" + request.getSurveyId() + ":question:" + questionId + ":results";

                logger.info("CorrelationID: {} - Processing vote for question: {}", correlationId, questionId);
                long newCount = redisTemplate.opsForHash().increment(redisKey, choiceId, 1);

                VoteUpdate voteUpdate = new VoteUpdate(correlationId, questionId, choiceId, newCount);

                try {
                    messageRedisTemplate.convertAndSend(channel, voteUpdate);
                    logger.info("CorrelationID: {} - Published vote update to channel {} for choice: {}",
                            correlationId, channel, choiceId);
                } catch (Exception e) {
                    logger.error("CorrelationID: {} - Failed to publish vote update: {}", correlationId, e.getMessage());
                }
            }
            logger.info("CorrelationID: {} - Processed vote for survey: {}", correlationId, request.getSurveyId());
        } else {
            logger.info("No active hosts viewing results for survey: {}, skipping Redis publish", request.getSurveyId());
        }
    }

}
