package com.ureca.child_recommend.config.embedding;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingOptionsBuilder;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmbeddingUtil {

    private final EmbeddingModel embeddingModel;

    public float[] createEmbedding(String input) {
        // Create the request for embedding with model specification
        EmbeddingRequest embeddingRequest = new EmbeddingRequest(
                List.of(input),
                EmbeddingOptionsBuilder.builder().build()
        );

        // Call the embedding API and get the response
        EmbeddingResponse embeddingResponse = this.embeddingModel.call(embeddingRequest);

        // Return the response as a Map
        return embeddingResponse.getResults().get(0).getOutput();
    }
}
