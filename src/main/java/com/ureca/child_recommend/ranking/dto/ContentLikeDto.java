package com.ureca.child_recommend.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContentLikeDto {
    private String title;
    private Double likes;
}
