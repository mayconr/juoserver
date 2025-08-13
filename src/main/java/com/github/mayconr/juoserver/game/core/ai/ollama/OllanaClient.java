package com.github.mayconr.juoserver.game.core.ai.ollama;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public interface OllanaClient {

    Response chat(List<Message> messages, int maxTokens);

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Message {
        private String role;
        private String content;
    }
}
