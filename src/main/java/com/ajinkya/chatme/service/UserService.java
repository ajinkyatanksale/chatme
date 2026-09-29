package com.ajinkya.chatme.service;

import com.ajinkya.chatme.dto.userInfo.User;
import com.ajinkya.chatme.exception.ResourceNotFoundException;
import com.ajinkya.chatme.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User getUserByUsername(String username) {
        Optional<com.ajinkya.chatme.entity.User> user = userRepository.findByUsername(username);
        User.UserBuilder userBuilder = User.builder();
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not present in system");
        } else {
            return userBuilder.userId(user.get().getId())
                    .avatarUrl(user.get().getAvatarUrl())
                    .status(user.get().getStatus().name())
                    .lastSeen(user.get().getLastSeen())
                    .username(user.get().getUsername())
                    .build();

        }
    }
}
