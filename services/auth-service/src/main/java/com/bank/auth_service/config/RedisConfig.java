package com.bank.auth_service.config;

import com.bank.auth_service.entity.OtpData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, OtpData> otpRedisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {

        RedisTemplate<String, OtpData> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        // Use a copy, not the shared bean - activating default typing here would
        // otherwise inject a "@class" property into every REST JSON response too.
        ObjectMapper mapper = objectMapper.copy();

        // Without this, Jackson doesn't embed a type hint in the JSON it writes
        // to Redis, so on read-back it can't tell what class to deserialize into
        // and falls back to a plain LinkedHashMap.
        mapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().allowIfSubType(Object.class).build(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(mapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }
}