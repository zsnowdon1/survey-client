package com.voting.survey_client.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voting.entities.SurveyDTO;
import com.voting.mongoData.Survey;
import com.voting.entities.VoteUpdate;
import com.voting.survey_client.dto.SurveyRequest;
import com.voting.survey_client.entity.SelectedChoice;
import com.voting.survey_client.service.SurveyService;
import com.voting.utils.SurveyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Primary
public class SurveyServiceImpl implements SurveyService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String SURVEY_CACHE_PREFIX = "Survey:";
    private static final Logger logger = LoggerFactory.getLogger(SurveyServiceImpl.class);

    public SurveyServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public SurveyDTO getSurvey(String accessCode) {
        Survey cachedSurvey = (Survey) redisTemplate.opsForValue().get(SURVEY_CACHE_PREFIX + accessCode);
        if (cachedSurvey == null) {
            throw new RuntimeException("Survey not found for access code: " + accessCode);
        }
        if (!"LIVE".equals(cachedSurvey.getStatus())) {
            throw new RuntimeException("Survey is not live for access code: " + accessCode);
        }
        return SurveyMapper.toDTOSurvey(cachedSurvey);
    }

    @Override
    public void postVote(SurveyRequest request) {
        String surveyId = request.getSurveyId();
        String activeHostsKey = "survey:" + surveyId + ":active-hosts";
        String resultsChannel = "survey:" + surveyId + ":results";

        // Check if any host is viewing results
        Long activeHosts = redisTemplate.opsForSet().size(activeHostsKey);
        boolean shouldPublish = activeHosts != null && activeHosts > 0;

        for (SelectedChoice choice : request.getResponses()) {
            String questionId = choice.getQuestionId();
            String choiceId = choice.getChoiceId();
            String redisKey = "survey:" + surveyId + ":question:" + questionId + ":results";

            logger.info("Processing vote for survey {}, question {}, choice {}", surveyId, questionId, choiceId);
            long newCount = redisTemplate.opsForHash().increment(redisKey, choiceId, 1);

            if (shouldPublish) {
                publishVoteUpdate(resultsChannel, questionId, choiceId, newCount);
            }
        }
    }

    private void publishVoteUpdate(String channel, String questionId, String choiceId, long votes) {
        try {
            VoteUpdate voteUpdate = new VoteUpdate(questionId, choiceId, votes);
            String updateMessage = objectMapper.writeValueAsString(voteUpdate);
            redisTemplate.convertAndSend(channel, updateMessage);
            logger.info("Published vote update to channel {}: {}", channel, updateMessage);
        } catch (Exception e) {
            logger.error("Failed to publish vote update to Redis channel {}: {}", channel, e.getMessage());
        }
    }

}
