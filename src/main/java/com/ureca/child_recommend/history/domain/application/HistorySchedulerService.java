package com.ureca.child_recommend.history.domain.application;

import com.ureca.child_recommend.child.domain.ChildMbtiScore;
import com.ureca.child_recommend.child.domain.Enum.ChildMbtiScoreStatus;
import com.ureca.child_recommend.child.domain.infrastructure.ChildMbtiScoreRepository;
import com.ureca.child_recommend.history.domain.History;
import com.ureca.child_recommend.history.domain.infrastructure.HistorySchedulerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class HistorySchedulerService {
    private final HistorySchedulerRepository historySchedulerRepository;
    private final ChildMbtiScoreRepository childMbtiScoreRepository;

    // history 테이블의 마지막 데이터와 child_mbti_score 테이블의 전 날 데이터와 비교
    // mbti score에 하나라도 변화가 있으면 insert
//    @Scheduled(cron = "0 */1 * * * ?") // 테스트용 매분마다 실행
    @Scheduled(cron = "0 0 0 * * ?")    // 매일 자정에 실행
    public void historySchedule() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // 어제의 ChildMbtiScore 데이터를 가져옴
        List<ChildMbtiScore> yesterdayScores = childMbtiScoreRepository.findByAssessmentDateAndStatus(yesterday, ChildMbtiScoreStatus.ACTIVE);

        for (ChildMbtiScore score : yesterdayScores) {
            // 해당 child의 history를 가져옴
            List<History> historyList = historySchedulerRepository.findByChildId(score.getChild().getId());

            // 최신 기록만 가져옴 (가장 최근 생성일자 기준으로 내림차순 정렬)
            History latestHistory = historyList.stream()
                    .sorted((h1, h2) -> h2.getCreateAt().compareTo(h1.getCreateAt()))
                    .findFirst()
                    .orElse(null);

            // 각각의 mbti 점수를 비교하여 하나라도 다르면 history에 삽입
            if (latestHistory == null || isScoreChanged(score, latestHistory)) {
                historySchedulerRepository.save(History.enrollToHistory(score));
            }
        }

    }

    private boolean isScoreChanged(ChildMbtiScore score, History history) {
        return !score.getEiScore().equals(history.getEiScore()) ||
                !score.getSnScore().equals(history.getSnScore()) ||
                !score.getTfScore().equals(history.getTfScore()) ||
                !score.getJpScore().equals(history.getJpScore());
    }

}
