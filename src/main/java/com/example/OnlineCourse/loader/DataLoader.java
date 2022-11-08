package com.example.OnlineCourse.loader;

import com.example.OnlineCourse.entity.User;
import com.example.OnlineCourse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    String init;   // update

    @Override
    public void run(String... args) throws Exception {
        try {
            if (init.equals("create") || init.equals("create-drop")) {
                User user=User.builder()
                        .role("OWNER")
                        .name("Nodirbek")
                        .chatId(5094739326L)
                        .step("START")
                        .build();
                userRepository.save(user);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
