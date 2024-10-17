package com.ureca.child_recommend.child.domain.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Data
public class MbtiDto {
    private int M;
    private int B;
    private int T;
    private int I;

    private String mbti1, mbti2, mbti3, mbti4;

    private String result;
}
