package com.ureca.child_recommend.contents.converter;

import com.ureca.child_recommend.contents.presentation.dto.ChatDto;

public class GptConverter {
    public static ChatDto.Response answer(String content) {
        return ChatDto.Response.builder()
                .message(content)
                .build();
    }
}
