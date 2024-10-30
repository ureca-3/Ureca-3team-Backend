package com.ureca.child_recommend.contents.application;

import com.ureca.child_recommend.config.gpt.GptWebClient;
import com.ureca.child_recommend.contents.domain.Contents;
import com.ureca.child_recommend.contents.domain.ContentsMbtiScore;
import com.ureca.child_recommend.contents.domain.Enum.ContentsStatus;
import com.ureca.child_recommend.contents.infrastructure.ContentsMbtiRepository;
import com.ureca.child_recommend.contents.infrastructure.ContentsRepository;
import com.ureca.child_recommend.contents.presentation.dto.ContentsDto;
import com.ureca.child_recommend.contents.presentation.dto.GptDto;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentsService {
    private final ContentsRepository contentsRepository;
    private final ContentsMbtiRepository mbtiRepository;

    private static final String USER = "user";
    private static final String ASSISTNAT = "assistant";
    private static final String SYSTEM = "system";

    private final GptWebClient gptWebClient;
    private final Map<Long, GptDto.Request> memberChatMap = new HashMap<>();
    private final ChannelTopic bookChannel;
    private final RedisTemplate redisTemplate;

    // 대화내용 삭제
    public void removeChat(Long userId) {
        if (!memberChatMap.containsKey(userId)) {
            throw new BusinessException(CommonErrorCode.GPT_SERVER_ERROR);
        }
        memberChatMap.remove(userId);
    }

    // 메시지 추가
    private void addChatMessages(GptDto.Request request, String role, String message) {
        request.addMessage(role, message);
    }

    public Contents saveContent(Long userId, ContentsDto.Request contentsRequest, ContentsMbtiScore mbtiScore, String mbtiResult) {
        Contents content = Contents.saveContents(contentsRequest, mbtiScore, mbtiResult);
        mbtiRepository.save(mbtiScore);
        contentsRepository.save(content);
        removeChat(userId);
        return content;
    }

    // 저장, 기본 데이터 입력 후 GPT 활용하여 mbti 데이터 저장
    @Transactional
    public Contents saveContents(Long userId, ContentsDto.Request request) {
        GptDto.Request gptRequest;
        if (memberChatMap.get(userId) == null) {
            gptRequest = gptWebClient.of(500);
            addChatMessages(gptRequest, SYSTEM, "너는 책 줄거리를 기반으로 MBTI 비율을 알려주는 ai야. " +
                    "줄거리를 기반으로 MBTI 성향을 전체 100%인 E와 I의 비율 합 중 E의 비율, 전체 100%인 S와 N의 비율 합 중 S의 비율, 전체 100%인 T와 F의 비율 합 중 T의 비율, 전체 100%인 J와 P의 비율 합 중 J의 비율을 구해줘.");
        } else {
            gptRequest = memberChatMap.get(userId);
        }

        String summary = request.getDescription();

        addChatMessages(gptRequest, USER, "'" + summary + "'" +
                "의 줄거리인 콘텐츠의 MBTI의 비율을 전체 100% 중 " +
                "E: {}%\n" +
                "S: {}%\n" +
                "T: {}%\n" +
                "J: {}%\n 형식으로 알려줘" );

        GptDto.Response gptResponse = gptWebClient.assistantRes(gptRequest);

        String content = gptResponse.getChoices().get(0).getMessage().getContent();
        addChatMessages(gptRequest, ASSISTNAT, content);
        memberChatMap.put(userId, gptRequest);

        String mbtiInfo = gptRequest.getMessages().get(2).content; // 질문에 대한 gpt 대답 데이터

        // 결과 mbti 파싱
        Pattern pattern = Pattern.compile("(\\d+)%"); // {}% 형식의 pattern 파악
        Matcher matcher = pattern.matcher(mbtiInfo); // 패턴에 매칭할 문자열을 입력해 Matcher 생성

        int eiPercentage = 0;
        int snPercentage = 0;
        int tfPercentage = 0;
        int jpPercentage = 0;

        int index = 0;
        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1)); // 매칭되는 문자열 중 1번째 그룹의 문자열 반환
            switch (index) {
                case 0 -> eiPercentage = value;
                case 1 -> snPercentage = value;
                case 2 -> tfPercentage = value;
                case 3 -> jpPercentage = value;
            }
            index++;
        }

        ContentsMbtiScore mbtiScore = ContentsMbtiScore.saveContentsMbti(eiPercentage, snPercentage, tfPercentage, jpPercentage);

        // mbti 결과 계산
        StringBuilder mbtiRes = new StringBuilder();
        if (eiPercentage > 50) mbtiRes.append("E"); else mbtiRes.append("I");
        if (snPercentage > 50) mbtiRes.append("S"); else mbtiRes.append("N");
        if (tfPercentage > 50) mbtiRes.append("T"); else mbtiRes.append("F");
        if (jpPercentage > 50) mbtiRes.append("J"); else mbtiRes.append("P");

        // 제목과 작가 확인 시 없으면 생성
        Contents savedContent = contentsRepository.findByTitleAndAuthor(request.getTitle(), request.getAuthor()).orElseGet(()
                -> saveContent(userId, request, mbtiScore, mbtiRes.toString()));

        // 📢 알림 발행: Redis 채널에 메시지 전송
/*        String message = String.format("New Contents: %s", savedContent.getTitle());
        redisTemplate.convertAndSend(bookChannel.getTopic(), message); // 알림 발송*/
// 1650 수정
        String message = String.format("{\"message\": \"New Content: %s\", \"contentId\": %d}", savedContent.getTitle(), savedContent.getId());
        redisTemplate.convertAndSend(bookChannel.getTopic(), message); // Redis로 전송


        return savedContent;
    }

    public ContentsDto.Response readContents(Long contentsId) {
        Contents findContents = contentsRepository.findById(contentsId).orElseThrow(()
                -> new BusinessException(CommonErrorCode.CONTENTS_NOT_FOUND));
        return ContentsDto.Response.contentsData(findContents);
    }

    @Transactional
    public ContentsDto.Response updateContents(Long contentsId, ContentsDto.Request request) { // 수정된 데이터가 존재하면 반영
        Contents findContents = contentsRepository.findById(contentsId).orElseThrow(()
                -> new BusinessException(CommonErrorCode.CONTENTS_NOT_FOUND));

        findContents.updateContents(request);
        return ContentsDto.Response.contentsData(findContents);
    }

    @Transactional
    public ContentsDto.Response deleteContents(Long contentsId) {
        Contents findContents = contentsRepository.findById(contentsId).orElseThrow(()
                -> new BusinessException(CommonErrorCode.CONTENTS_NOT_FOUND));

        findContents.updateStatus(ContentsStatus.NONACTIVE);
        return ContentsDto.Response.contentsData(findContents);
    }

    // 컨텐츠 검색
    public List<Contents> searchContents(String keyword) {
        List<Contents> searchContents = contentsRepository.findByTitleContainingOrAuthorContaining(keyword, keyword);
        if (searchContents.isEmpty()) throw new BusinessException(CommonErrorCode.CONTENTS_NOT_FOUND);

        return searchContents;
    }
}
