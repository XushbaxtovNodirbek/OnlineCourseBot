package com.example.OnlineCourse.repository;

import com.example.OnlineCourse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByChatId(Long chatId);
    List<User> findAllByRole(String Role);
}
