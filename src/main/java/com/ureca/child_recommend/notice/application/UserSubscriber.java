package com.ureca.child_recommend.notice.application;

import com.ureca.child_recommend.notice.presentation.SseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class UserSubscriber implements MessageListener {

    private final SseController sseController;

    @Autowired
    public UserSubscriber(SseController sseController) {
        this.sseController = sseController;
    }

    @Override
    public void onMessage(Message message, byte[] pattern){
        // redis한테 받은 메시지
        String notification = new String(message.getBody());
        System.out.println("새 알림: " + notification);

        // SSE를 통해 클라이언트에게 알림 전송
        sseController.sendSseEvent(notification);
    }

}
