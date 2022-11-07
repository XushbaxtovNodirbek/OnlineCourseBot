package com.example.OnlineCourse.service;

import com.example.OnlineCourse.entity.User;

import java.util.List;

public interface UserService {
    User saveUser(String name,Long chatId);
    User findById(Long chatId);
    List<User> getAll();
}
