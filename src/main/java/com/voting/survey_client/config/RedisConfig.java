package com.voting.survey_client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

@Configuration
public class RedisConfig {

    private final String hostname;

    private final int port;

    public RedisConfig(@Value("${spring.redis.host}") String hostname, @Value("${spring.redis.port}") int port) {
        this.hostname = hostname;
        this.port = port;
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(hostname, port);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

//    @Bean
//    public RedisTemplate<String, Object> messageRedisTemplate() {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(jedisConnectionFactory());
//
//        // Use Jackson2JsonRedisSerializer for pub/sub messaging
//        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
//
//        template.setValueSerializer(serializer);
//        template.setHashValueSerializer(serializer);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.afterPropertiesSet();
//
//        return template;
//    }

    @Bean
    public DefaultRedisScript<Object> redisScript() {
        DefaultRedisScript<Object> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/PostVote.lua")));
        redisScript.setResultType(Object.class);
        return redisScript;
    }
}
