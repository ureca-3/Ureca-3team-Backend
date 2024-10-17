package com.ureca.child_recommend.notice.application;

import com.ureca.child_recommend.contents.domain.Contents;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic bookChannel;

/* // 리팩토링
        public BookService(RedisTemplate<String, Object> redisTemplate, ChannelTopic bookChannel) {
        this.redisTemplate = redisTemplate;
        this.bookChannel = bookChannel;
    }

    public void registerNewBook(Contents contents){
        // Redis에 새 책 등록 이벤트 발행
        String message = "아이들을 위한 새 책이 등록되었습니다.: " + contents.getTitle();
        redisTemplate.convertAndSend("bookChannel", message);
    }*/
    public void registerNewBook(Contents contents) {
        // 책 제목이 공란인지 확인 (예외 처리)
        if(contents.getTitle() == null || contents.getTitle().isBlank()) {
            throw new BusinessException(CommonErrorCode.TEST_NOT_FOUND);
        }

        // Redis에 새 책 등록 이벤트 발행
        String message = "아이들을 위한 새 책이 등록되었습니다.: " + contents.getTitle();
        redisTemplate.convertAndSend(bookChannel.getTopic(), message); // 객체를 이용한 채널 관리.

    }


}
