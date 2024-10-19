package com.ureca.child_recommend.notice.presentation;

import com.ureca.child_recommend.notice.application.SseEmitterManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class SseController {
/*  // 리팩토링 -> SseEmitterManager로 이동 / 기능 분할
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/sse/notifications")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(3600 * 1000L); // 1시간
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
    }*/

    private final SseEmitterManager sseEmitterManager;

    public SseController(SseEmitterManager sseEmitterManager) {
        this.sseEmitterManager = sseEmitterManager;
    }

    @GetMapping("/sse/notifications")
    public SseEmitter subscribe(){
        return sseEmitterManager.createEmitter();
    }

    public void sendSseEvent(String notification){
        sseEmitterManager.sendContentNotification(notification);
    }


}
