package com.ureca.child_recommend.contents.presentation.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class GptDto {

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private List<Choice> choices;
        @Data @NoArgsConstructor @AllArgsConstructor
        public static class Choice {
            private int index;
            private Message message;
        }
    }

    @Data
    public static class Request {
        private String model;
        private List<Message> messages;

        public Request(String model, String summary) {
            this.model = model;
            this.messages =  new ArrayList<>();
            this.messages.add(new Message("user", summary));
        }
    }
}
