package com.ajinkya.chatme.service;

import com.ajinkya.chatme.dto.userInfo.User;
import com.ajinkya.chatme.exception.ResourceNotFoundException;
import com.ajinkya.chatme.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User getUserByUsername(String username) {
        Optional<com.ajinkya.chatme.entity.User> user = userRepository.findByUsername(username);
        User.UserBuilder userBuilder = User.builder();
        user.orElseThrow(() -> new ResourceNotFoundException("User not present in system"));

        return userBuilder.userId(user.get().getId())
                .avatarUrl(user.get().getAvatarUrl())
                .status(user.get().getStatus().name())
                .lastSeen(user.get().getLastSeen())
                .username(user.get().getUsername())
                .name(user.get().getName())
                .mobileNumber(user.get().getMobileNumber())
                .build();
    }

    public List<User> getDynamicUsersByUsernamePrefix(String query, String username) {
        List<com.ajinkya.chatme.entity.User> user = userRepository.searchByEmailPrefix(query,
                username,
                PageRequest.of(0, 5));

        User.UserBuilder userBuilder = User.builder();

        List<User> userList =new ArrayList<>();

        user.forEach((user1) -> {
            User userInfo = userBuilder.userId(user1.getId())
                    .avatarUrl(user1.getAvatarUrl())
                    .status(user1.getStatus().name())
                    .lastSeen(user1.getLastSeen())
                    .username(user1.getUsername())
                    .name(user1.getName())
                    .mobileNumber(user1.getMobileNumber())
                    .build();
            userList.add(userInfo);
        });

        return userList;
    }
}
