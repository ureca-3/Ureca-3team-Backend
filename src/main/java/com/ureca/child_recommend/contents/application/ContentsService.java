package com.ureca.child_recommend.contents.application;

import com.ureca.child_recommend.contents.domain.Contents;
import com.ureca.child_recommend.contents.domain.ContentsMbtiScore;
import com.ureca.child_recommend.contents.domain.Enum.ContentsStatus;
import com.ureca.child_recommend.contents.infrastructure.ContentsMbtiRepository;
import com.ureca.child_recommend.contents.infrastructure.ContentsRepository;
import com.ureca.child_recommend.contents.presentation.dto.ContentsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ContentsService {
    private final ContentsRepository contentsRepository;
    private final ContentsMbtiRepository mbtiRepository;
    private final ContentsMbtiService mbtiService;
    private final ChannelTopic bookChannel;
    private final RedisTemplate<String, Object> redisTemplate;

    // 저장, 기본 데이터 입력 후 GPT 활용하여 mbti 데이터 저장
    public Long saveContents(ContentsDto.Request request) {

        // 줄거리 데이터 활용
        String summary = request.getDescription();

        String mbtiInfo = mbtiService.chatWithGpt("'" + summary + "'" +
                "의 줄거리인 콘텐츠의 MBTI의 비율을 E: {}%\n" +
                "S: {}%\n" +
                "T: {}%\n" +
                "J: {}%\n 형식으로 알려줘" );

        // 결과 mbti 파싱
        Pattern pattern = Pattern.compile("(\\d+)%");
        Matcher matcher = pattern.matcher(mbtiInfo);

        int eiPercentage = 0;
        int snPercentage = 0;
        int tfPercentage = 0;
        int jpPercentage = 0;

        int index = 0;
        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            switch (index) {
                case 0 -> eiPercentage = value;
                case 1 -> snPercentage = value;
                case 2 -> tfPercentage = value;
                case 3 -> jpPercentage = value;
            }
            index++;
        }

        ContentsMbtiScore mbtiScore = ContentsMbtiScore.builder()
                .eiScore(eiPercentage)
                .snScore(snPercentage)
                .tfScore(tfPercentage)
                .jpScore(jpPercentage)
                .build();

        mbtiRepository.save(mbtiScore);

        // mbti 결과 계산
        StringBuilder mbtiRes = new StringBuilder();
        if (eiPercentage > 50) mbtiRes.append("E"); else mbtiRes.append("I");
        if (snPercentage > 50) mbtiRes.append("S"); else mbtiRes.append("N");
        if (tfPercentage > 50) mbtiRes.append("T"); else mbtiRes.append("F");
        if (jpPercentage > 50) mbtiRes.append("J"); else mbtiRes.append("P");

        // 제목과 작가 확인
        if (contentsRepository.findByTitleAndAuthor(request.getTitle(), request.getAuthor()) == null) {
            Contents contents = Contents.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .author(request.getAuthor())
                    .publisher(request.getPublisher())
                    .publicationYear(request.getPublicationYear())
                    .contentsMbtiResult(mbtiRes.toString())
                    .status(ContentsStatus.ACTIVE)
                    .contentsMbti(mbtiScore)
                    .build();

            // 📢 알림 발행: Redis 채널에 메시지 전송
            String message = String.format("새로운 컨텐츠가 등록되었습니다: %s by %s",
                    contents.getTitle(), contents.getAuthor());
            redisTemplate.convertAndSend(bookChannel.getTopic(), message);

            return contentsRepository.save(contents).getId();
        }
        else {
            return contentsRepository.findByTitleAndAuthor(request.getTitle(), request.getAuthor()).getId();
        }
    }

    public Contents readContents(Long contentsId) {
        Contents findContents = contentsRepository.findById(contentsId).get();

        return findContents;
    }

    public Contents updateContents(Long contentsId, ContentsDto.Request request) {
        Contents findContents = contentsRepository.findById(contentsId).get();
        Contents updateContent = Contents.builder()
                .id(contentsId)
                .title(request.getTitle())
                .description(request.getDescription())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .publicationYear(request.getPublicationYear())
                .contentsMbtiResult(findContents.getContentsMbtiResult())
                .status(ContentsStatus.ACTIVE)
                .contentsMbti(findContents.getContentsMbti())
                .build();

        contentsRepository.save(updateContent);
        return updateContent;
    }

    public ContentsDto.Response deleteContents(Long contentsId) {
        Contents findContents = contentsRepository.findById(contentsId).get();
        Contents deleteContent = Contents.builder()
                .id(contentsId)
                .title(findContents.getTitle())
                .description(findContents.getDescription())
                .author(findContents.getAuthor())
                .publisher(findContents.getPublisher())
                .publicationYear(findContents.getPublicationYear())
                .contentsMbtiResult(findContents.getContentsMbtiResult())
                .status(ContentsStatus.NONACTIVE)
                .contentsMbti(findContents.getContentsMbti())
                .build();

        contentsRepository.save(deleteContent);
        return ContentsDto.Response.contentsData(deleteContent);
    }

}
