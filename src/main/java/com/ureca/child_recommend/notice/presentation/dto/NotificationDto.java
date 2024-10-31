package com.ureca.child_recommend.notice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationDto {
    private Long contentId;
    private String title;
    private String message;
}
