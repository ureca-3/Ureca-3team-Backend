package com.ureca.child_recommend.contents.application;

import com.ureca.child_recommend.child.infrastructure.ChildRepository;
import com.ureca.child_recommend.child.presentation.dto.ContentsRecommendDto;
import com.ureca.child_recommend.config.embedding.EmbeddingUtil;
import com.ureca.child_recommend.config.gpt.GptWebClient;
import com.ureca.child_recommend.config.redis.util.RedisUtil;
import com.ureca.child_recommend.contents.domain.Contents;
import com.ureca.child_recommend.contents.domain.ContentsMbtiScore;
import com.ureca.child_recommend.contents.domain.ContentsVector;
import com.ureca.child_recommend.contents.domain.Enum.ContentsStatus;
import com.ureca.child_recommend.contents.infrastructure.ContentsMbtiRepository;
import com.ureca.child_recommend.contents.infrastructure.ContentsRepository;
import com.ureca.child_recommend.contents.infrastructure.ContentsVectorRepository;
import com.ureca.child_recommend.contents.presentation.dto.ContentsDto;
import com.ureca.child_recommend.contents.presentation.dto.GptDto;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import com.ureca.child_recommend.relation.FeedBack;
import com.ureca.child_recommend.relation.infrastructure.FeedBackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentsService {
    private final ContentsRepository contentsRepository;
    private final ContentsMbtiRepository mbtiRepository;
    private final ChildRepository childRepository;
    private final EmbeddingUtil embeddingUtil;
    private final RedisUtil redisUtil;
    private final ContentsVectorRepository contentsVectorRepository;
    private final FeedBackRepository feedBackRepository;

    private static final String USER = "user";
    private static final String ASSISTNAT = "assistant";
    private static final String SYSTEM = "system";

    private final GptWebClient gptWebClient;
    private final Map<Long, GptDto.Request> memberChatMap = new HashMap<>();
    private final ChannelTopic bookChannel;
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
                    "줄거리를 기반으로 MBTI 성향을 전체 100%인 E와 I의 비율 합 중 E의 비율, 전체 100%인 S와 N의 비율 합 중 S의 비율, 전체 100%인 T와 F의 비율 합 중 T의 비율, 전체 100%인 J와 P의 비율 합 중 J의 비율을 구해줘." +
                    "MBTI 특징은 아래와 같으니, 해당 특징들로 비율을 계산해줘" +
                    "INFJ : 차가운, 알 수 없는, 미묘한, 따뜻한, 감성적인\n" +
                    "INFP : 감성적인, 나른한, 우울한, 따뜻한, 눈물이 많은, 요정같은\n" +
                    "INTJ : 무심한, 냉정한, 시니컬한, 지적인, 은근 다정한, 생각이 깊은\n" +
                    "INTP :영리한, 괴짜같은, 건조한, 기계같은, 지적인, 너드한, 무심한, 창의적인\n" +
                    "ISTP : 시니컬한, 피곤한, 귀찮음이 많은, 쿨한, 무관심한, 사회적 거리두기\n" +
                    "ISFP : 중심적인, 예술적인, 호기심이 많은, 인간 고양이\n" +
                    "ISTJ : 분석적인, 규칙적인, 규율적인, 모범적인, 무뚝뚝한\n" +
                    "ISFJ :수호적인, 선한, 구호적인, 친절한, 따스한, 영역이 넓은\n" +
                    "ENFJ : 온화한, 다정한, 부드러운, 지혜로운, 따스한, 모범적인\n" +
                    "ENFP : 긍정적인, 친절한, 사교적인, 활발한, 해피 바이러스\n" +
                    "ENTJ :냉담한, 냉정한, 지적인, 호탕한, 쿨한, 지휘적인\n" +
                    "ENTP : 유머러스한, 쿨한, 말이 많은, 뒷심 부족한, 주도적인, 창의적인\n" +
                    "ESTJ : 냉담한, 냉철한, 지적인, 지도적인, 지휘적인, 관리자같은\n" +
                    "ESFJ : 선한, 구호적인, 인맥이 넓은, 모범적인, 사교적인, 소통적인\n" +
                    "ESTP : 호탕한, 직설적인, 은근 냉담한, 유머러스한, 쿨한, 모험적인\n" +
                    "ESFP : 사교적인, 쾌활한, 항상 바쁜, 인싸같은, 친구가 많은, 입담이 뛰어난\n" +
                    "\n");
        } else {
            gptRequest = memberChatMap.get(userId);
        }

        String summary = request.getDescription();

        addChatMessages(gptRequest, USER, "'" + summary + "'" +
                "의 줄거리인 콘텐츠의 MBTI의 비율을 전체 100% 중 " +
                "E: {}%\n" +
                "S: {}%\n" +
                "T: {}%\n" +
                "J: {}%\n 형식으로 알려주는데, 각 값 0이랑 100은 절대 주지마" );

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

        //임베딩벡터 생성
        inputEmbedding(savedContent.getId());


        // 📢 알림 발행: Redis 채널에 메시지 전송
        String message = String.format("New Contents: %s", savedContent.getTitle());
        redisUtil.sendNotified(bookChannel.getTopic(),message);

        return savedContent;
    }

    public ContentsDto.Response readContents(Long contentsId) {
        Contents findContents = contentsRepository.findById(contentsId).orElseThrow(()
                -> new BusinessException(CommonErrorCode.CONTENTS_NOT_FOUND));

        return ContentsDto.Response.contentsData(findContents, findContents.getContentsMbti());
    }

    @Transactional
    public ContentsDto.Response updateContents(Long contentsId, ContentsDto.Request request) { // 수정된 데이터가 존재하면 반영
        Contents findContents = contentsRepository.findById(contentsId).orElseThrow(()
                -> new BusinessException(CommonErrorCode.CONTENTS_NOT_FOUND));

        findContents.updateContents(request);
        return ContentsDto.Response.contentsData(findContents, findContents.getContentsMbti());
    }

    @Transactional
    public ContentsDto.Response deleteContents(Long contentsId) {
        Contents findContents = contentsRepository.findById(contentsId).orElseThrow(()
                -> new BusinessException(CommonErrorCode.CONTENTS_NOT_FOUND));

        findContents.updateStatus(ContentsStatus.NONACTIVE);
        return ContentsDto.Response.contentsData(findContents, findContents.getContentsMbti());
    }

    // 컨텐츠 검색 - active인 상태만
    @Transactional
    public List<ContentsDto.Response> searchContents(String keyword) {
        List<Contents> searchContents = contentsRepository.findByStatusAndTitleContaining(ContentsStatus.ACTIVE, keyword);
        if (searchContents.isEmpty() || keyword.equals("")) {
            throw new BusinessException(CommonErrorCode.CONTENTS_NOT_FOUND);
        }

        // Contents 리스트를 ContentsDto.Response 리스트로 변환
        return searchContents.stream()
                .map(ContentsDto.Response::contentsSingleData) // Contents를 ContentsDto.Response로 매핑
                .collect(Collectors.toList());
    }


    // 컨텐츠 리스트 페이지 처리 - 5개씩 (최신 데이터)
    public List<ContentsDto.Response> getAllContents() {
        return contentsRepository.findAll().stream()
                .map(ContentsDto.Response::contentsSingleData)
                .collect(Collectors.toList());
    }



    private void inputEmbedding(Long contentsId) {
        Contents contents = contentsRepository.findById(contentsId).orElseThrow(() -> new BusinessException(CommonErrorCode.CONTENTS_NOT_FOUND));

        GptDto.Request gptRequest;

        gptRequest = gptWebClient.of(500);
        addChatMessages(gptRequest, SYSTEM, "당신은 키워드를 추출하고 텍스트를 요약하는 작업을 수행하는 도우미입니다." +
                " 주어진 텍스트에서 가장 중요한 다섯 개의 키워드를 제공하고, 다음 형식으로 요약하세요:\n" +
                "키워드: [키워드 목록]\n" +
                "요약: [두 문장 요약]\n");
        addChatMessages(gptRequest, USER, contents.getDescription());

        GptDto.Response gptResponse = gptWebClient.assistantRes(gptRequest);
        String content = gptResponse.getChoices().get(0).getMessage().getContent();

        // 키워드와 요약본을 분리하기 위해 먼저 줄 바꿈(\n)으로 나눕니다.
        String[] lines = content.split("\n");

        // 각 줄에서 키워드와 요약본을 추출합니다.
        String keywords = "";
        String summary = "";

        // 키워드와 요약본을 찾아서 변수에 저장
        for (String line : lines) {
            if (line.startsWith("키워드:")) {
                keywords = line.substring("키워드:".length()).trim();
            } else if (line.startsWith("요약:")) {
                summary = line.substring("요약:".length()).trim();
            }
        }

        // String.format을 사용하여 최종 문자열 생성
        String input = String.format("책 제목: %s, 키워드: %s, 저자: %s, 요약본: %s, MBTI: %s, %s, %s, %s",
                contents.getTitle(),
                keywords,
                contents.getAuthor(),
                summary,
                contents.getContentsMbti().getEiScore() + "%",
                contents.getContentsMbti().getSnScore() + "%",
                contents.getContentsMbti().getTfScore() + "%",
                contents.getContentsMbti().getJpScore() + "%");

        System.out.println(input);

        //임베딩 벡터 생성
        float[] contentsEmbedding = embeddingUtil.createEmbedding(input);

        saveContentsEmbedding(contentsEmbedding,contents);

    }

    protected void saveContentsEmbedding(float[] contentsEmbedding, Contents contents){

        ContentsVector contentsVector = ContentsVector.createContentsVector(contentsEmbedding,contents);
        contentsVectorRepository.save(contentsVector);

    }


    public List<ContentsRecommendDto.Response.SimilarBookDto> seachUserLikeContentsSim(Long userId, Long childId) {
        childRepository.findByIdAndUserId(childId,userId).orElseThrow(() -> new BusinessException(CommonErrorCode.CHILD_NOT_FOUND));

        List<FeedBack> feedBackList = feedBackRepository.findTop5LikesByChildId(childId);

        //  각 피드백의 임베딩 벡터 추출
        List<Long> contentsIdLists = feedBackList.stream()
                .map(feedback -> feedback.getContents().getId())
                .toList();


        List<Long> VectorcontentsIdList = contentsVectorRepository.findSimilarContentsByAverageEmbedding(contentsIdLists);

        List<Contents> contentsList = contentsRepository.findByIdIn(VectorcontentsIdList);

        return contentsList.stream()
                        .map(o-> ContentsRecommendDto.Response.SimilarBookDto.of(o.getId(),o.getTitle(),o.getPosterUrl()))
                .collect(Collectors.toList());
    }
}
