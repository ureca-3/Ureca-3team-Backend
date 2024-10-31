package com.ureca.child_recommend.viewing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContentLikeDto {


    private String title;
    private Long contentId;
    private String posterUrl;
    private Double likes;
}