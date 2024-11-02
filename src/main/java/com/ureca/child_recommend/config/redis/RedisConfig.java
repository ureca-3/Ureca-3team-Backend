package com.ureca.child_recommend.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.child_recommend.child.presentation.dto.ContentsRecommendDto;
import com.ureca.child_recommend.notice.application.UserSubscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;


import java.util.List;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

//    @Value("${spring.data.redis.password}")
//    private String redisPassword;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(host);
        redisConfig.setPort(port);

//        // 비밀번호가 설정되어 있으면 비밀번호도 추가
//        if (!redisPassword.isEmpty()) {
//            redisConfig.setPassword(RedisPassword.of(redisPassword));
//        }

        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, List<ContentsRecommendDto.Response.SimilarBookDto>> jsonRedisTemplate() {
        RedisTemplate<String, List<ContentsRecommendDto.Response.SimilarBookDto>> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());

        // ObjectMapper를 통해 TypeReference로 리스트 타입 지원
        ObjectMapper objectMapper = new ObjectMapper();
        Jackson2JsonRedisSerializer<List<ContentsRecommendDto.Response.SimilarBookDto>> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper.getTypeFactory().constructCollectionType(List.class, ContentsRecommendDto.Response.SimilarBookDto.class));

        template.setValueSerializer(serializer);
        return template;
    }

    @Bean
    public ChannelTopic bookChannel() {
        return new ChannelTopic("bookChannel");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            UserSubscriber userSubscriber,
            ChannelTopic bookChannel) {
        // Redis 메시지 리스너 컨테이너 설정
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(userSubscriber, bookChannel);
        return container;
    }

    // ZSet Operations (좋아요 순위 작업용)
    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }


}


