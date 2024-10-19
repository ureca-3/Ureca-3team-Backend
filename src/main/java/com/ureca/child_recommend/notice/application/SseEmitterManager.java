package com.ureca.child_recommend.notice.application;

import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseEmitterManager {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();


    public SseEmitter createEmitter(){
//        SseEmitter emitter = new SseEmitter(3600 * 1000L); // 1시간
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // Endless Connection
        emitters.add(emitter);

        // 클라이언트 연결 종료 시 제거
        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            System.out.println("Emitter completed and removed.");
        });
        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            System.out.println("Emitter timed out and removed.");
        });

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

    @Scheduled(fixedRate = 120000) // 2 분마다 heartbeat 전송
    public void sendHeartbeat() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send("heartbeat", MediaType.TEXT_EVENT_STREAM);
                System.out.println("Sent heartbeat to emitter."); // 로그 추가
            } catch (Exception e) {
                emitters.remove(emitter);  // 실패 시 emitter 제거
                System.err.println("Error sending heartbeat: " + e.getMessage()); // 오류 로그 추가
            }
        }
    }


}
