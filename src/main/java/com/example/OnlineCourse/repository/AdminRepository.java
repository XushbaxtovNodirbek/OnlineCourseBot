package com.example.OnlineCourse.repository;

import com.example.OnlineCourse.entity.Admins;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admins,Long> {
    Admins findAdminsByChatId(Long chatId);
}
