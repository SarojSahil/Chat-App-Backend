package com.sahil.chatapp.service;

import com.sahil.chatapp.dto.ContactDeleteRequest;
import com.sahil.chatapp.dto.ContactResponse;
import com.sahil.chatapp.dto.ContactSaveRequest;
import com.sahil.chatapp.dto.ContactUpdateRequest;
import com.sahil.chatapp.exception.ContactAlreadyExistsException;
import com.sahil.chatapp.exception.ContactNotFoundException;
import com.sahil.chatapp.exception.UserNotFoundException;
import com.sahil.chatapp.model.Contact;
import com.sahil.chatapp.model.User;
import com.sahil.chatapp.repository.ContactRepository;
import com.sahil.chatapp.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    public ContactService(ContactRepository contactRepository, UserRepository userRepository, EntityManager entityManager) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    public List<ContactResponse> getContacts(User user) {
        return contactRepository.findByOwnerId(user.getId())
                .stream()
                .map(projection ->
                        ContactResponse
                                .builder()
                                .id(projection.getId())
                                .name(projection.getName())
                                .user(ContactResponse.ContactUser
                                        .builder()
                                        .id(projection.getUserId())
                                        .name(projection.getUserName())
                                        .phoneNumber(projection.getUserPhoneNumber())
                                        .build())
                                .build()
                )
                .toList();
    }

    @Transactional
    public ContactResponse createContact(User user, ContactSaveRequest request) {

        String phone = request.getPhoneNumber();

        UserRepository.UserProjection result = userRepository.findUserMinimalByPhoneNumber(phone)
                .orElseThrow(() -> new UserNotFoundException("User does not exists."));

        User contactUser = entityManager.getReference(User.class, result.getId());

        if (result.getId().equals(user.getId())) {
            throw new IllegalArgumentException("You cannot add yourself as a contact.");
        }

        Contact contact = Contact.builder()
                .owner(user)
                .contactUser(contactUser)
                .name(request.getName().trim())
                .createdAt(LocalDateTime.now())
                .build();

        try {
            Contact savedContact = contactRepository.save(contact);

            return ContactResponse
                    .builder()
                    .id(savedContact.getId())
                    .name(savedContact.getName())
                    .user(ContactResponse.ContactUser
                            .builder()
                            .id(result.getId())
                            .name(result.getName())
                            .phoneNumber(result.getPhoneNumber())
                            .build())
                    .build();
        } catch (Exception e) {
            throw new ContactAlreadyExistsException("Contact already exists.");
        }
    }

    @Transactional
    public void deleteContact(User user, ContactDeleteRequest request) {
        int modified = contactRepository.deleteByIdAndOwnerId(request.getId(), user.getId());
        if (modified == 0) {
            throw new ContactNotFoundException("Contact does not exist.");
        }
    }

    @Transactional
    public ContactResponse updateContact(User user, ContactUpdateRequest request) {

        Contact contact = contactRepository.findByIdAndOwnerId(request.getContactId(), user.getId())
                .orElseThrow(() -> new ContactNotFoundException("Contact not found"));

        contact.setName(request.getContactName().trim());

        return ContactResponse
                .builder()
                .id(contact.getId())
                .name(contact.getName())
                .user(ContactResponse.ContactUser
                        .builder()
                        .id(contact.getContactUser().getId())
                        .name(contact.getContactUser().getName())
                        .phoneNumber(contact.getContactUser().getPhoneNumber())
                        .build())
                .build();
    }
}