package com.whl.spring.ai.demo.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.ollama.autoconfigure.OllamaConnectionProperties;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ollama")
public class OllamaController {
    private final OllamaConnectionProperties properties;

    private final OllamaApi ollamaApi;

    public OllamaController(OllamaConnectionProperties properties, OllamaApi ollamaApi) {
        this.properties = properties;
        this.ollamaApi = ollamaApi;
    }

    @GetMapping("")
    public String index() {
        return "Hello, I'm Ollama!";
    }

    @GetMapping("/listModels")
    public OllamaApi.ListModelResponse listModels() {
        return this.ollamaApi.listModels();
    }

    @GetMapping("/ai/generate")
    public ChatResponse generate(@RequestParam String model, @RequestParam String message) {
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl(this.properties.getBaseUrl())
                        .build())
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(model)
                        .build())
                .build();
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.call(prompt);
    }

}
