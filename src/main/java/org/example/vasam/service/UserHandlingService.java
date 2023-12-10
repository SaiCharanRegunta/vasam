package org.example.vasam.service;

import org.example.vasam.repository.UserRepository;
import org.springframework.stereotype.Service;


@Service
public class UserHandlingService {
    private final UserRepository userRepository;

    public UserHandlingService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public int getTotalUserCount() {
        return userRepository.getTotalUserCount();
    }

    public boolean doesUserExists(String userId) {
        return userRepository.doesUserExists(userId);
    }

    public void register(String userId) {
        userRepository.save(userId);
    }
}
