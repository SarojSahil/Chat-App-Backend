package com.sahil.chatapp.controller;

import com.sahil.chatapp.dto.GenerateMessage;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/generate")
public class MessageGenerator {

    private final ChatClient chatClient;

    public MessageGenerator(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @PostMapping
    public ResponseEntity<GenerateMessage> generate(@RequestBody GenerateMessage request) throws Exception {

        String output = chatClient.prompt()
                .system("User types a messsage in chat application's input and he wants you to improvise, fix grammar, and generate message based on the person he is talking to in short if not explicitly said give long text. Dont use words like here is your message etc user shoudl be able to send your response directly to the other person without any changes so dont make it like an ai response , feels natural.")
                .user(request.getMessage())
                .call()
                .content();

        return ResponseEntity.ok(new GenerateMessage(output));
    }
}