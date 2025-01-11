package com.voting.survey_client.service.impl;

import com.voting.entities.SurveyDTO;
import com.voting.mongoData.Survey;
import com.voting.survey_client.dao.SurveyRepository;
import com.voting.survey_client.mongoData.SurveyMapper;
import com.voting.survey_client.service.SurveyService;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Primary
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository surveyRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SURVEY_CACHE_PREFIX = "Survey:";

    public SurveyServiceImpl(SurveyRepository surveyRepository, RedisTemplate<String, Object> redisTemplate) {
        this.surveyRepository = surveyRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public SurveyDTO getSurvey(String id) {
        // Create ID for redis ex: Survey:id and checks for it
        String cacheKey = SURVEY_CACHE_PREFIX + id;
        Survey cachedSurvey = (Survey) redisTemplate.opsForValue().get(cacheKey);
        if(cachedSurvey != null) {
            return SurveyMapper.toDTOSurvey(cachedSurvey);
        }

        // If not in redis, pull from MongoDB
        Optional<Survey> optionalSurvey = this.surveyRepository.findById(id);
        if(optionalSurvey.isEmpty()) {
            throw new RuntimeException("Survey not found with id: " + id);
        }

        // Store in redis
        Survey mongoSurvey = optionalSurvey.get();
        redisTemplate.opsForValue().set(cacheKey, mongoSurvey, 1, TimeUnit.HOURS);
        return SurveyMapper.toDTOSurvey(mongoSurvey);
    }
}
