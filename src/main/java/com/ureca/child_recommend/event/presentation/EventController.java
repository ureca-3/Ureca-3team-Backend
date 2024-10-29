package com.ureca.child_recommend.event.presentation;


import com.ureca.child_recommend.event.application.EventService;
import com.ureca.child_recommend.event.presentation.dto.EventDto;
import com.ureca.child_recommend.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/event") // 해당 event 조회 api
    public SuccessResponse<EventDto.Response> findEvent(@RequestBody Long eventId){
        EventDto.Response response = eventService.getEvent(eventId);
        return SuccessResponse.success(response);
    }

}
