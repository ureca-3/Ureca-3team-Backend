package com.ureca.child_recommend.notice.presentation;

import com.ureca.child_recommend.config.redis.util.RedisUtil;
import com.ureca.child_recommend.notice.application.SseEmitterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
//@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class SseController {
/* // 해당 코드로 그라운르 룰 준수 불가.
      지속적인 연결 유지 위해서는 SseEmitter 직접 반환해 연결을 상시 유지 필요
    private final SseEmitterManager sseEmitterManager;

    @GetMapping("/newbook")
    public SuccessResponse<SseEmitter> subscribe(){
        SseEmitter emitter = sseEmitterManager.createEmitter();
        return SuccessResponse.success(emitter);
    }

    public void sendSseEvent(String notification){
        sseEmitterManager.sendContentNotification(notification);
    }

}*/

    private final SseEmitterManager sseEmitterManager;
    private final RedisUtil redisUtil;

    private static final String NOTIFICATION_KEY = "notifications";


@Autowired
public SseController(SseEmitterManager sseEmitterManager, RedisUtil redisUtil) {
    this.sseEmitterManager = sseEmitterManager;
    this.redisUtil = redisUtil;
}

@GetMapping("/newbook")
public SseEmitter subscribe(@RequestParam("token") String token){
    return sseEmitterManager.createEmitter();
}

public void sendSseEvent(String notification){
    sseEmitterManager.sendContentNotification(notification);
}

    @GetMapping("/previous")
    public List<Object> getPreviousNotifications() {
        // RedisUtil을 통해 Redis에 저장된 모든 이전 알림을 가져옵니다.
        return redisUtil.getListRange(NOTIFICATION_KEY, 0, -1);
    }

}


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
