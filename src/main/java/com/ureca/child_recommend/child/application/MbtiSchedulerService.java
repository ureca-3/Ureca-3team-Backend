package com.ureca.child_recommend.child.application;

import com.ureca.child_recommend.child.domain.Enum.ChildMbtiScoreStatus;
import com.ureca.child_recommend.child.infrastructure.ChildMbtiScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MbtiSchedulerService {

    private final ChildMbtiScoreRepository childMbtiScoreRepository;

//    @Scheduled(cron = "0 */1 * * * ?") // 테스트용 매분마다 실행
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void deleteMbtiScore(){
        LocalDateTime thresholdDateTime = LocalDateTime.now().minusDays(30);  // 30일 전 시간 계산
        childMbtiScoreRepository.deleteByUpdateAtAndStatus(thresholdDateTime, ChildMbtiScoreStatus.DELETE);
    }
}
