package com.ureca.child_recommend.contents.application;

import com.ureca.child_recommend.contents.presentation.dto.GptDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service @RequiredArgsConstructor
public class ContentsMbtiService {
    @Value("${web.gpt.model}")
    private String model;

    @Value("${web.gpt.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate restTemplate;

    public String chatWithGpt(String summary) {
        GptDto.Request request = new GptDto.Request(model, summary);
        GptDto.Response response = restTemplate.postForObject(apiURL, request, GptDto.Response.class);

        return response.getChoices().get(0).getMessage().getContent();
    }
}
