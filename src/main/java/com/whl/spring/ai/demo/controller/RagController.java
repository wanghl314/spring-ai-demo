package com.whl.spring.ai.demo.controller;

import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreProperties;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.JedisPooled;

import java.io.File;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rag")
public class RagController {
    private final VectorStore vectorStore;

    private final JedisPooled jedis;

    private final RedisVectorStoreProperties redisVectorStoreProperties;

    private final OpenAiChatModel chatModel;

    public RagController(VectorStore vectorStore,
                         JedisPooled jedis,
                         RedisVectorStoreProperties redisVectorStoreProperties,
                         OpenAiChatModel chatModel) {
        this.vectorStore = vectorStore;
        this.jedis = jedis;
        this.redisVectorStoreProperties = redisVectorStoreProperties;
        this.chatModel = chatModel;
    }

    @GetMapping("")
    public String index() {
        return "Hello, I'm RAG";
    }

    @GetMapping("/add")
    public String add() throws Exception {
        File file = new File("C:\\Users\\Administrator\\Desktop\\Linux操作系统查看服务器出口IP地址.docx");
        TikaDocumentReader reader = new TikaDocumentReader(new FileSystemResource(file));
        TokenTextSplitter splitter = new TokenTextSplitter(300, 200, 10, 400, true);
        List<Document> documents = splitter.apply(reader.get());
        documents.forEach(document -> {
            document.getMetadata().put("id", document.getId());
            vectorStore.add(List.of(document));
        });
        return "SUCCESS";
    }

    @GetMapping("/exists/{id}")
    public String exists(@PathVariable String id) throws Exception {
        return this.jedis.exists(this.redisVectorStoreProperties.getPrefix() + id) ? "EXISTS" : "NOT EXISTS";
    }

    @GetMapping("/ai/generate")
    public String generate(@RequestParam String message) throws Exception {
        List<Document> documents = this.vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .topK(3)
                        .build());
        String promptString = """
                你是一个智能助手，你可以根据下面搜索到的内容回复用户
                ### 用户的问题是
                %s
                ### 具体内容
                %s
                """;
        promptString = String.format(promptString, message,
                Optional.ofNullable(documents).map(List::getFirst).map(Document::getText).orElse(""));
        System.out.println(promptString);
        Prompt prompt = new Prompt(promptString);
        ChatResponse response = this.chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }

}
