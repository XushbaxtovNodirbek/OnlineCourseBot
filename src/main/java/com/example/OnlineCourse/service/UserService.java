package com.example.OnlineCourse.service;

import com.example.OnlineCourse.entity.User;
import com.example.OnlineCourse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public boolean saveUser(String name,Long id){
        try{
            User user = User.builder()
                    .chatId(id)
                    .name(name)
                    .build();

            userRepository.save(user);
            return true;
        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }
}
