package com.sahil.chatapp.controller;

import com.sahil.chatapp.dto.ContactDeleteRequest;
import com.sahil.chatapp.dto.ContactResponse;
import com.sahil.chatapp.dto.ContactSaveRequest;
import com.sahil.chatapp.dto.ContactUpdateRequest;
import com.sahil.chatapp.model.User;
import com.sahil.chatapp.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public ResponseEntity<List<ContactResponse>> getContacts(@AuthenticationPrincipal User user) {
        List<ContactResponse> contacts = contactService.getUserContacts(user);
        return ResponseEntity.ok(contacts);
    }

    @PostMapping
    public ResponseEntity<ContactResponse> saveContact(@AuthenticationPrincipal User user, @RequestBody @Valid ContactSaveRequest request) {
        ContactResponse savedContact = contactService.saveUserContact(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedContact);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteContact(@AuthenticationPrincipal User user, @RequestBody @Valid ContactDeleteRequest request) {
        contactService.deleteContact(user, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<ContactResponse> updateContact(@AuthenticationPrincipal User user, @RequestBody @Valid ContactUpdateRequest request) {
        ContactResponse updatedContact = contactService.updateContact(user, request);
        return ResponseEntity.ok(updatedContact);
    }
}

