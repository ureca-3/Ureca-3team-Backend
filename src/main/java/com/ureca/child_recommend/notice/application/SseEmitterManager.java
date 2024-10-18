package com.ureca.child_recommend.notice.application;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseEmitterManager {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();


    public SseEmitter createEmitter(){
        SseEmitter emitter = new SseEmitter(3600 * 1000L); // 1시간
        emitters.add(emitter);

        // 클라이언트 연결 종료 시 제거
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    public void sendEvent(String notification){
        for(SseEmitter emitter : emitters){
            try{
                emitter.send(notification, MediaType.TEXT_EVENT_STREAM);
            } catch (Exception e){
                emitters.remove(emitter);
            }
        }
    }

}
