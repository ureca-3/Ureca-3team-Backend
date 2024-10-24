package com.ureca.child_recommend.contents.presentation.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class GptDto {

    @Data @NoArgsConstructor @AllArgsConstructor @Getter
    public static class Response {
        private List<Choice> choices;
        @Data @NoArgsConstructor @AllArgsConstructor @Getter
        public static class Choice {
            private int index;
            private Message message;
        }
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Request {
        private String model;
        private List<Message> messages;
        private float temperature;
        private int maxTokens;

        public Request(String model, Integer maxTokens) {
            this.model = model;
            this.messages = new ArrayList<>();
            this.maxTokens = maxTokens;
            this.temperature = 0.8f;
        }
        public void addMessage(String role, String message) {
            this.messages.add(new Message(role, message));
        }
        public static Request of(String model, int maxTokens) {
            return new Request(model, maxTokens);
        }
    }
}
