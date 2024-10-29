package com.ureca.child_recommend.event.application;

import com.ureca.child_recommend.event.domain.Event;
import com.ureca.child_recommend.event.domain.WinnerLog;
import com.ureca.child_recommend.event.infrastructure.EventRepository;
import com.ureca.child_recommend.event.presentation.dto.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public void makeNewEvent(EventDto.Request eventRequest) {
        Event event = Event.builder()
                .name(eventRequest.getName())
                .date(eventRequest.getDate())
                .description(eventRequest.getDescription())
                .build();

        // Event 객체를 데이터베이스에 저장
        eventRepository.save(event);
    }
}
