package com.ureca.child_recommend.event.presentation;


import com.ureca.child_recommend.event.application.EventService;
import com.ureca.child_recommend.event.domain.Event;
import com.ureca.child_recommend.event.presentation.dto.EventDto;
import com.ureca.child_recommend.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class EventController {

    private final EventService eventService;

    @PostMapping("/event") // event 생성 api
    public SuccessResponse<String> makeEvent(@RequestBody EventDto.Request eventRequest) {
        eventService.makeNewEvent(eventRequest);
        return SuccessResponse.successWithoutResult(null);
    }
    @GetMapping("/event/{id}") // 해당 event 조회 API
    public SuccessResponse<EventDto.Response> findEvent(@PathVariable Long id) {
        EventDto.Response response = eventService.getEvent(id);
        return SuccessResponse.success(response);
    }


    @GetMapping("/event")
    public SuccessResponse<List<Event>> findAllEvent(){
        List<Event> responselist = eventService.getAllEvent();
        return SuccessResponse.success(responselist);
    }

}
