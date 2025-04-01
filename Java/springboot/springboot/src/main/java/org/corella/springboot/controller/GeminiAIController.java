package org.corella.springboot.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GeminiAIController {

    private final ChatClient chatClient;

    public GeminiAIController(ChatClient.Builder chatClientbuilder) {
        this.chatClient = chatClientbuilder.build();
    }

    @GetMapping("/ai")
    String generation(@RequestParam("userInput") String userInput) {
        return this.chatClient.prompt()
                .user(userInput)
                .call()
                .content();
    }
}
