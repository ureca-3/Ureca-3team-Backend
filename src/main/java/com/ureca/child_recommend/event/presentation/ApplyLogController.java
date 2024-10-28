package com.ureca.child_recommend.event.presentation;

import com.ureca.child_recommend.event.application.ApplyLogService;
import com.ureca.child_recommend.event.domain.ApplyLog;
import com.ureca.child_recommend.event.domain.Enum.ApplyLogStatus;
import com.ureca.child_recommend.event.infrastructure.EventRepository;
import com.ureca.child_recommend.user.domain.Users;
import com.ureca.child_recommend.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ApplyLogController {

    private final ApplyLogService applyLogService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @PostMapping("/apply-logs")
    public ResponseEntity<ApplyLog> createApplyLog(@RequestParam String name, @RequestParam String phone, @RequestParam Long userId) {
        ApplyLog applyLog = ApplyLog.builder()
                .name(name)
                .phone(phone)
                .log(LocalDateTime.now())
                .status(ApplyLogStatus.DEFAULT)
                .user(userRepository.findById(userId).get()) // userId로 User 객체 생성 (User 엔티티가 필요함)
                .event(eventRepository.findEventByDate(LocalDate.now()).get())
                .build();
        applyLogService.executeWithLock(userId,applyLog);
        return ResponseEntity.ok(applyLog);
    }//심재민   박진 정현수  이민석   이병준 홍민기   나균안 이인복 이승헌 정성종


}

