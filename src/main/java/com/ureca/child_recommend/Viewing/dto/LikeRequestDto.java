package com.ureca.child_recommend.Viewing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequestDto {
    private Long childId; // 좋아요를 누른 자녀의 ID
}
