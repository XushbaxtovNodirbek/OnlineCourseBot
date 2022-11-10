package com.example.OnlineCourse.service;

import com.example.OnlineCourse.entity.AdminTmp;

import java.util.List;

public interface AdminTmpService {
    List<AdminTmp> getAll();
    AdminTmp findByChatId(Long chatId);
    void deleteTmp(Long chatId);
    AdminTmp saveAdminTmp(String name,String userName,Long chatId);
}
