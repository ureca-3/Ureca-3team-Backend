package com.ureca.child_recommend.notice.application;

import com.ureca.child_recommend.contents.domain.Contents;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic bookChannel;

    public BookService(RedisTemplate<String, Object> redisTemplate, ChannelTopic bookChannel) {
        this.redisTemplate = redisTemplate;
        this.bookChannel = bookChannel;
    }

    public void registerNewBook(Contents contents){
        // Redis에 새 책 등록 이벤트 발행
        String message = "아이들을 위한 새 책이 등록되었습니다.: " + contents.getTitle();
        redisTemplate.convertAndSend("bookChannel", message);
    }

}
