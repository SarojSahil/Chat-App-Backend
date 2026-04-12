package com.sahil.chatapp.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sahil.chatapp.model.User;
import com.sahil.chatapp.repository.UserRepository;

@Service
public class  UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        return userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new UsernameNotFoundException("User does not exists."));
    }
}
