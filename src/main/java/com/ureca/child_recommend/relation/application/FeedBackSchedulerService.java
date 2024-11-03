package com.ureca.child_recommend.relation.application;

import com.ureca.child_recommend.child.presentation.dto.ContentsRecommendDto;
import com.ureca.child_recommend.config.redis.util.RedisUtil;
import com.ureca.child_recommend.contents.domain.Contents;
import com.ureca.child_recommend.contents.infrastructure.ContentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.ureca.child_recommend.relation.application.FeedBackService.REDIS_KEY;


@Service
@RequiredArgsConstructor
@Slf4j
public class FeedBackSchedulerService {
    private final ContentsRepository contentsRepository;

    private final RedisUtil redisUtil;

    public static final String TODAY_LIKE_CONTENTS = "today_like_contents";



    @Scheduled(cron = "0 58 * * * ?")     // 매 시간 58분에 상위 도서 저장 실행
    private void scheduleViewRankSearch(){
        List<Long> contentsIdList = redisUtil.getTop10LikedContentsToday(REDIS_KEY);

        List<Contents> contentsList = contentsRepository.findByIdIn(contentsIdList);

        List<ContentsRecommendDto.Response.SimilarBookDto> similarBookDtoList = contentsList.stream()
                .map(o-> ContentsRecommendDto.Response.SimilarBookDto.of(o.getId(),o.getTitle(),o.getPosterUrl()))
                .collect(Collectors.toList());

        redisUtil.saveBooks(TODAY_LIKE_CONTENTS , similarBookDtoList);
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행(스코어 기록 삭제)
    private void deleteTodayLikedBooks(){
        redisUtil.deleteScoreData(REDIS_KEY + "*");
        System.out.println("Deleted all entries for key: " + REDIS_KEY);    }
}
