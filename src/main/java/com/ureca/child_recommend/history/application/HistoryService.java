package com.ureca.child_recommend.history.application;

import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import com.ureca.child_recommend.history.domain.History;
import com.ureca.child_recommend.history.infrastructure.HistoryRepository;
import com.ureca.child_recommend.history.presentation.dto.MbtiHistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        LocalDate now = LocalDate.now(); // 오늘 날짜 계산
        LocalDateTime startDateTime;

        switch (type) {
           case "daily":
               LocalDate startDate = now.minusDays(6);
               startDateTime = startDate.atStartOfDay(); // 시간 00:00:00 추가
               histories = historyRepository.findTop7ByChildIdAndCreateAtGreaterThanEqualOrderByCreateAtAsc(child_id, startDateTime); // 최근 7일간의 데이터
               return getDailyData(histories);
           case "weekly":
               startDateTime = now.minusWeeks(3).with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).atStartOfDay();
               histories = historyRepository.findByChildIdAndCreateAtGreaterThanEqual(child_id, startDateTime); // 최근 4주간의 데이터
               return getWeeklyAverageData(histories, now);
           case "monthly":
               startDateTime = now.minusMonths(5).with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
               histories = historyRepository.findByChildIdAndCreateAtGreaterThanEqual(child_id, startDateTime); // 최근 6개월의 데이터
               return getMonthlyAverageData(histories, now);
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

    private MbtiHistoryDto.Response.HistoryDto getWeeklyAverageData(List<History> histories, LocalDate now) {
        List<Integer> eiScores = new ArrayList<>();
        List<Integer> snScores = new ArrayList<>();
        List<Integer> tfScores = new ArrayList<>();
        List<Integer> jpScores = new ArrayList<>();
        List<String> dayList = new ArrayList<>();

        for(int i=0; i<4; i++){
            // 현재 날짜에서 i주 전의 월요일과 일요일을 계산
            LocalDate startOfWeek = now.minusWeeks(i).with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            LocalDate endOfWeek = startOfWeek.plus(6, ChronoUnit.DAYS);

            // 주차별 날짜 리스트
            String formattedWeekRange = startOfWeek.getMonthValue() + "." + startOfWeek.getDayOfMonth() +
                    "~" + endOfWeek.getMonthValue() + "." + endOfWeek.getDayOfMonth();
            dayList.add(formattedWeekRange);

            // 각 주에 해당하는 데이터 필터링
            List<History> weeklyData = histories.stream()
                    .filter(h -> !h.getCreateAt().toLocalDate().isBefore(startOfWeek) &&
                                 !h.getCreateAt().toLocalDate().isAfter(endOfWeek))
                    .collect(Collectors.toList());

            addAverageScores(weeklyData, eiScores, snScores, tfScores, jpScores);
        }
        reverseAll(eiScores, snScores, tfScores, jpScores, dayList);
        return MbtiHistoryDto.Response.HistoryDto.of(eiScores, snScores, tfScores, jpScores, dayList);
    }

    private MbtiHistoryDto.Response.HistoryDto getMonthlyAverageData(List<History> histories, LocalDate now) {
        List<Integer> eiScores = new ArrayList<>();
        List<Integer> snScores = new ArrayList<>();
        List<Integer> tfScores = new ArrayList<>();
        List<Integer> jpScores = new ArrayList<>();
        List<String> dayList = new ArrayList<>();

        // 최근 6개월의 데이터를 계산
        for (int i = 0; i < 6; i++) {
            // i개월 전의 첫번째 날짜와 마지막 날짜 계산
            LocalDate startOfMonth = now.minusMonths(i).with(TemporalAdjusters.firstDayOfMonth());
            LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());

            // 월별 날짜 리스트 (n월 형식)
            String monthLabel = startOfMonth.getMonthValue() + "월";
            dayList.add(monthLabel);

            // 각 월에 해당하는 데이터 필터링
            List<History> monthlyData = histories.stream()
                    .filter(h -> !h.getCreateAt().toLocalDate().isBefore(startOfMonth) &&
                            !h.getCreateAt().toLocalDate().isAfter(endOfMonth))
                    .collect(Collectors.toList());

            addAverageScores(monthlyData, eiScores, snScores, tfScores, jpScores);
        }
        reverseAll(eiScores, snScores, tfScores, jpScores, dayList);
        return MbtiHistoryDto.Response.HistoryDto.of(eiScores, snScores, tfScores, jpScores, dayList);

    }

    // 특정 기간의 데이터를 각 요소별 리스트에 평균을 계산하여 추가
    private void addAverageScores(List<History> data, List<Integer> eiScores, List<Integer> snScores,
                                  List<Integer> tfScores, List<Integer> jpScores) {
        eiScores.add(calculateAverage(data, History::getEiScore));
        snScores.add(calculateAverage(data, History::getSnScore));
        tfScores.add(calculateAverage(data, History::getTfScore));
        jpScores.add(calculateAverage(data, History::getJpScore));
    }

    // 각 요소별 평균 계산
    private Integer calculateAverage(List<History> histories, java.util.function.ToIntFunction<History> mapper) {
        return histories.stream()
                .collect(Collectors.averagingInt(mapper))
                .intValue();
    }

    // 역순으로 나열
    private void reverseAll(List<?>... lists) {
        for (List<?> list : lists) {
            Collections.reverse(list);
        }
    }
}
