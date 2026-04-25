package com.sahil.chatapp.controller;

import com.sahil.chatapp.dto.ConversationResponse;
import com.sahil.chatapp.dto.MessageSendRequest;
import com.sahil.chatapp.model.User;
import com.sahil.chatapp.repository.MessageRepository;
import com.sahil.chatapp.service.ConversationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/conversation")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getConversations(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(conversationService.getConversations(user));
    }

    @GetMapping("/{conversationId}/message")
    public ResponseEntity<Slice<MessageRepository.MessageProjection>> getMessages(@AuthenticationPrincipal User user, @PathVariable("conversationId") Long conversationId, @RequestParam("page") int page, @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(conversationService.getMessages(user, conversationId, pageable));
    }

    @PostMapping("/message")
    public ResponseEntity<ConversationResponse> sendMessage(@AuthenticationPrincipal User user, @RequestBody @Valid MessageSendRequest request) {
        return ResponseEntity.ok(conversationService.sendMessage(user, request));
    }
}
