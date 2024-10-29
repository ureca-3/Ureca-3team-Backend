package com.ureca.child_recommend.history.application;

import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import com.ureca.child_recommend.history.domain.History;
import com.ureca.child_recommend.history.infrastructure.HistoryRepository;
import com.ureca.child_recommend.history.presentation.dto.MbtiHistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;

    public MbtiHistoryDto.Response.HistoryDto getHistoryData(Long child_id, String type) {
        List<History> histories;

       switch (type) {
           case "daily":
               LocalDate startDate = LocalDate.now().minusDays(6);
               histories = historyRepository.findTop7ByChildIdOrderByCreateAtDesc(child_id, startDate); // 최근 7일간의 데이터
               return getDailyData(histories);
           case "weekly":
               histories = historyRepository.findAllByChildId(child_id);
               return getWeeklyAverageData(histories);
           case "monthly":
               histories = historyRepository.findAllByChildId(child_id);
               return getMonthlyAverageData(histories);
           default:
               throw new BusinessException(CommonErrorCode.HISTORY_NOT_FOUND);
       }

    }


    private MbtiHistoryDto.Response.HistoryDto getDailyData(List<History> histories) {
        // 각 점수 필드를 추출하여 List<Integer> 형식으로 변환
        List<Integer> eiScores = histories.stream().map(History::getEiScore).collect(Collectors.toList());
        List<Integer> snScores = histories.stream().map(History::getSnScore).collect(Collectors.toList());
        List<Integer> tfScores = histories.stream().map(History::getTfScore).collect(Collectors.toList());
        List<Integer> jpScores = histories.stream().map(History::getJpScore).collect(Collectors.toList());

        // 날짜 리스트 생성
        List<String> dayList = histories.stream()
                .map(h -> h.getCreateAt().toLocalDate().toString())  // LocalDate 형식으로 변환
                .collect(Collectors.toList());

        return MbtiHistoryDto.Response.HistoryDto.of(eiScores, snScores, tfScores, jpScores, dayList);

    }

    private MbtiHistoryDto.Response.HistoryDto getWeeklyAverageData(List<History> histories) {
        LocalDate now = LocalDate.now();
        List<List<History>> weeklyData = new ArrayList<>();
        List<String> dayList = new ArrayList<>();

        for(int i=0; i<4; i++){
            // 현재 날짜에서 i주 전의 월요일과 일요일을 계산
            LocalDate startOfWeek = now.minusWeeks(i).with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            LocalDate endOfWeek = startOfWeek.plus(6, ChronoUnit.DAYS);

            // 주차별 날짜 리스트
            String formattedWeekRange = startOfWeek.getMonthValue() + "." + startOfWeek.getDayOfMonth() +
                    "~" + endOfWeek.getMonthValue() + "." + endOfWeek.getDayOfMonth();
            dayList.add(formattedWeekRange);

            // 데이터의 생성날짜가 해당 범위에 있는지 확인하는 필터링 작업
            List<History> weekData = histories.stream()
                    .filter(h -> !h.getCreateAt().toLocalDate().isBefore(startOfWeek) &&
                                 !h.getCreateAt().toLocalDate().isAfter(endOfWeek))
                    .collect(Collectors.toList());
            weeklyData.add(weekData);

        }

        // 각 주차별 평균 계산
        List<Integer> eiScores = weeklyData.stream()
                .map(this::calculateAverageEiScore)
                .collect(Collectors.toList());

        List<Integer> snScores = weeklyData.stream()
                .map(this::calculateAverageSnScore)
                .collect(Collectors.toList());

        List<Integer> tfScores = weeklyData.stream()
                .map(this::calculateAverageTfScore)
                .collect(Collectors.toList());

        List<Integer> jpScores = weeklyData.stream()
                .map(this::calculateAverageJpScore)
                .collect(Collectors.toList());

        Collections.reverse(eiScores);
        Collections.reverse(snScores);
        Collections.reverse(tfScores);
        Collections.reverse(jpScores);
        Collections.reverse(dayList);

        return MbtiHistoryDto.Response.HistoryDto.of(eiScores, snScores, tfScores, jpScores, dayList);
    }

    // 각 점수별 평균 계산 메서드
    private Integer calculateAverageEiScore(List<History> histories) {
        return histories.stream()
                .collect(Collectors.averagingInt(History::getEiScore))
                .intValue();
    }

    private Integer calculateAverageSnScore(List<History> histories) {
        return histories.stream()
                .collect(Collectors.averagingInt(History::getSnScore))
                .intValue();
    }

    private Integer calculateAverageTfScore(List<History> histories) {
        return histories.stream()
                .collect(Collectors.averagingInt(History::getTfScore))
                .intValue();
    }

    private Integer calculateAverageJpScore(List<History> histories) {
        return histories.stream()
                .collect(Collectors.averagingInt(History::getJpScore))
                .intValue();
    }

    private MbtiHistoryDto.Response.HistoryDto getMonthlyAverageData(List<History> histories) {
        LocalDate now = LocalDate.now();
        List<List<History>> monthlyData = new ArrayList<>();
        List<String> dayList = new ArrayList<>();

        // 최근 6개월의 데이터를 계산
        for (int i = 0; i < 6; i++) {
            // i개월 전의 첫번째 날짜와 마지막 날짜 계산
            LocalDate startOfMonth = now.minusMonths(i).with(TemporalAdjusters.firstDayOfMonth());
            LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());

            // 월별 날짜 리스트 (n월 형식)
            String monthLabel = startOfMonth.getMonthValue() + "월";
            dayList.add(monthLabel);

            // 데이터의 생성날짜가 해당 범위에 있는지 확인하는 필터링 작업
            List<History> monthData = histories.stream()
                    .filter(h -> !h.getCreateAt().toLocalDate().isBefore(startOfMonth) &&
                            !h.getCreateAt().toLocalDate().isAfter(endOfMonth))
                    .collect(Collectors.toList());
            monthlyData.add(monthData);
        }
        // 각 월별 평균 계산
        List<Integer> eiScores = monthlyData.stream()
                .map(this::calculateAverageEiScore)
                .collect(Collectors.toList());

        List<Integer> snScores = monthlyData.stream()
                .map(this::calculateAverageSnScore)
                .collect(Collectors.toList());

        List<Integer> tfScores = monthlyData.stream()
                .map(this::calculateAverageTfScore)
                .collect(Collectors.toList());

        List<Integer> jpScores = monthlyData.stream()
                .map(this::calculateAverageJpScore)
                .collect(Collectors.toList());

        Collections.reverse(eiScores);
        Collections.reverse(snScores);
        Collections.reverse(tfScores);
        Collections.reverse(jpScores);
        Collections.reverse(dayList);

        return MbtiHistoryDto.Response.HistoryDto.of(eiScores, snScores, tfScores, jpScores, dayList);

    }
}
