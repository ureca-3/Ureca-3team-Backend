package com.ureca.child_recommend.notice.presentation;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class SseController {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/sse/notifications")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);

        // 클라이언트 연결 종료 시 제거
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    public void sendSseEvent(String notification){
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(notification, MediaType.TEXT_EVENT_STREAM);
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        }
    }

}
