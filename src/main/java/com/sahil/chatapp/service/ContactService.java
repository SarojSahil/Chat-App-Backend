package com.sahil.chatapp.service;

import com.sahil.chatapp.dto.ContactDeleteRequest;
import com.sahil.chatapp.dto.ContactResponse;
import com.sahil.chatapp.dto.ContactSaveRequest;
import com.sahil.chatapp.dto.ContactUpdateRequest;
import com.sahil.chatapp.exception.UserNotFoundException;
import com.sahil.chatapp.model.Contact;
import com.sahil.chatapp.model.User;
import com.sahil.chatapp.repository.ContactRepository;
import com.sahil.chatapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public ContactService(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    public List<ContactResponse> getUserContacts(User user) {
        List<Contact> contacts = contactRepository.findByOwnerId(user.getId());
        return contacts
                .stream()
                .map(c -> new ContactResponse(c.getId(), c.getName(), c.getContactUser().getPhoneNumber()))
                .toList();
    }

    public ContactResponse saveUserContact(User user, ContactSaveRequest request) {

        String phone = request.getPhoneNumber().trim();

        User contactUser = userRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new UserNotFoundException("User does not exist."));

        if (contactUser.getId().equals(user.getId())) {
            throw new IllegalArgumentException("You cannot add yourself as a contact.");
        }

        if (contactRepository.existsByOwnerIdAndContactUserId(user.getId(), contactUser.getId())) {
            throw new IllegalArgumentException("Contact already exists.");
        }

        Contact contact = Contact.builder()
                .owner(user)
                .contactUser(contactUser)
                .name(request.getName().trim())
                .createAt(LocalDateTime.now())
                .build();

        Contact savedContact = contactRepository.save(contact);

        return ContactResponse
                .builder()
                .id(savedContact.getId())
                .name(savedContact.getName())
                .phoneNumber(contactUser.getPhoneNumber())
                .build();
    }

    public void deleteContact(User user, ContactDeleteRequest request) {

        Contact contact = contactRepository.findByIdAndOwnerId(request.getId(), user.getId())
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        contactRepository.deleteById(contact.getId());
    }

    public ContactResponse updateContact(User user, ContactUpdateRequest request) {

        Contact contact = contactRepository.findByIdAndOwnerId(request.getId(), user.getId())
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        contact.setName(request.getName().trim());

        Contact updated = contactRepository.save(contact);

        return ContactResponse
                .builder()
                .id(updated.getId())
                .name(updated.getName())
                .phoneNumber(updated.getContactUser().getPhoneNumber())
                .build();
    }
}