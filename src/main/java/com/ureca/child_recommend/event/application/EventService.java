package com.ureca.child_recommend.event.application;

import com.ureca.child_recommend.config.redis.util.RedisUtil;
import com.ureca.child_recommend.event.domain.Event;
import com.ureca.child_recommend.event.infrastructure.EventRepository;
import com.ureca.child_recommend.event.presentation.dto.EventDto;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final RedisUtil redisUtil;

    private final ChannelTopic eventTopic;

    @Transactional
    public void makeNewEvent(EventDto.Request eventRequest) {
        Event event = Event.builder()
                .name(eventRequest.getName())
                .date(eventRequest.getDate())
                .description(eventRequest.getDescription())
                .build();

        String notificationMessage = "이벤트가 등록되었어요. 서둘러서 참여해주세요."; String message = String.format("{\"message\": \"%s\", \"eventId\": %d}", notificationMessage, event.getId());
        redisUtil.sendNotified(eventTopic.getTopic(), message);
        // Event 객체를 데이터베이스에 저장
        eventRepository.save(event);
    }

    public EventDto.Response getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.EVENT_NOT_FOUND));

        // Event를 EventDto.Response로 변환
        return EventDto.Response.builder()
                .name(event.getName())
                .date(event.getDate())
                .description(event.getDescription())
                .build();
    }

    public List<Event> getAllEvent() {
          List<Event> events = eventRepository.findAll();
          return events;
//          List<EventDto.Response> responses = new ArrayList<>();
//          for(Event event : events) {
//              EventDto.Response responselist = EventDto.Response.builder()
//                      .name(event.getName())
//                      .date(event.getDate())
//                      .description(event.getDescription())
//                      .build();
//              responses.add(responselist);
//          }
//          return responses;
    }
}
