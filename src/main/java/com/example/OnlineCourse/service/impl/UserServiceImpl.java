package com.example.OnlineCourse.service.impl;

import com.example.OnlineCourse.entity.User;
import com.example.OnlineCourse.repository.UserRepository;
import com.example.OnlineCourse.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User saveUser(String name, Long chatId) {
        User user=User.builder()
                .step("START")
                .role("USER")
                .chatId(chatId)
                .name(name)
                .build();
        userRepository.save(user);
        return user;
    }

    @Override
    public User findById(Long chatId) {
        return userRepository.findByChatId(chatId);
    }

    @Override
    public List<User> getAll() {
        return null;
    }
}
