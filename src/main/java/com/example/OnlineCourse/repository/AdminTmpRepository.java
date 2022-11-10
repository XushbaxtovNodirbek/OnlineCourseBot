package com.example.OnlineCourse.repository;

import com.example.OnlineCourse.entity.AdminTmp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminTmpRepository extends JpaRepository<AdminTmp,Long> {
    AdminTmp findByChatId(Long chatId);
}
