package com.ureca.child_recommend.contents.application;

import com.ureca.child_recommend.contents.domain.Contents;
import com.ureca.child_recommend.contents.domain.ContentsMbtiScore;
import com.ureca.child_recommend.contents.domain.Enum.ContentsStatus;
import com.ureca.child_recommend.contents.infrastructure.ContentsMbtiRepository;
import com.ureca.child_recommend.contents.infrastructure.ContentsRepository;
import com.ureca.child_recommend.contents.presentation.dto.ContentsDto;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentsService {
    private final ContentsRepository contentsRepository;
    private final ContentsMbtiRepository mbtiRepository;
    private final ContentsMbtiService mbtiService;

    public Contents saveContent(ContentsDto.Request contentsRequest, ContentsMbtiScore mbtiRequest, String mbtiResult) {
        Contents content = Contents.saveContents(contentsRequest, mbtiRequest, mbtiResult);
        return contentsRepository.save(content);
    }

    // 저장, 기본 데이터 입력 후 GPT 활용하여 mbti 데이터 저장
    @Transactional
    public void saveContents(ContentsDto.Request request) {
        // 줄거리 데이터 활용
        String summary = request.getDescription();

        String mbtiInfo = mbtiService.chatWithGpt("'" + summary + "'" +
                "의 줄거리인 콘텐츠의 MBTI의 비율을 E: {}%\n" +
                "S: {}%\n" +
                "T: {}%\n" +
                "J: {}%\n 형식으로 알려줘" );

        System.out.println(mbtiInfo);

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

        ContentsMbtiScore mbtiScore = ContentsMbtiScore.saveContentsMbti(eiPercentage, snPercentage, tfPercentage, jpPercentage);

        mbtiRepository.save(mbtiScore);

        // mbti 결과 계산
        StringBuilder mbtiRes = new StringBuilder();
        if (eiPercentage > 50) mbtiRes.append("E"); else mbtiRes.append("I");
        if (snPercentage > 50) mbtiRes.append("S"); else mbtiRes.append("N");
        if (tfPercentage > 50) mbtiRes.append("T"); else mbtiRes.append("F");
        if (jpPercentage > 50) mbtiRes.append("J"); else mbtiRes.append("P");

        // 제목과 작가 확인 시 없으면 생성
        contentsRepository.findByTitleAndAuthor(request.getTitle(), request.getAuthor()).orElseGet(() -> saveContent(request, mbtiScore, mbtiRes.toString()));
    }

    public ContentsDto.Response readContents(Long contentsId) {
        Contents findContents = contentsRepository.findById(contentsId).orElseThrow(() -> new BusinessException(CommonErrorCode.CONTENTS_NOT_FOUND));
        return ContentsDto.Response.contentsData(findContents);
    }

    @Transactional
    public ContentsDto.Response updateContents(Long contentsId, ContentsDto.Request request) {
        Contents findContents = contentsRepository.findById(contentsId).orElseThrow(()-> new BusinessException(CommonErrorCode.CONTENTS_NOT_FOUND));

        findContents.updateContents(request.getTitle(), request.getPosterUrl(), request.getDescription(),
                request.getAuthor(), request.getPublisher(), request.getPublicationYear(), ContentsStatus.ACTIVE);

        return ContentsDto.Response.contentsData(findContents);
    }

    @Transactional
    public ContentsDto.Response deleteContents(Long contentsId) {
        Contents findContents = contentsRepository.findById(contentsId).orElseThrow(()-> new BusinessException(CommonErrorCode.CONTENTS_NOT_FOUND));

        findContents.updateContents(findContents.getTitle(), findContents.getPosterUrl(), findContents.getDescription(),
                findContents.getAuthor(), findContents.getPublisher(), findContents.getPublicationYear(), ContentsStatus.NONACTIVE);
        return ContentsDto.Response.contentsData(findContents);
    }

}
