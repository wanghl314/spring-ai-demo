package com.whl.spring.ai.demo.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/deepseek")
public class DeepSeekController {
    private final OpenAiChatModel chatModel;

    public DeepSeekController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("")
    public String index() {
        return "Hello, I'm DeepSeek R1!";
    }

    @GetMapping("/ai/generate")
    public ChatResponse generate(@RequestParam(value = "message", defaultValue = "给我讲个笑话") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return this.chatModel.call(prompt);
    }

    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "给我讲个笑话") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return this.chatModel.stream(prompt);
    }

}
