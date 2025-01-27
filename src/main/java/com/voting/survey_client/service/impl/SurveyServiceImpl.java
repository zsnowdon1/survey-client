package com.voting.survey_client.service.impl;

import com.voting.entities.SurveyDTO;
import com.voting.mongoData.Survey;
import com.voting.survey_client.service.SurveyService;
import com.voting.utils.SurveyMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Primary
public class SurveyServiceImpl implements SurveyService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SURVEY_CACHE_PREFIX = "Survey:";

    public SurveyServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
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

}
