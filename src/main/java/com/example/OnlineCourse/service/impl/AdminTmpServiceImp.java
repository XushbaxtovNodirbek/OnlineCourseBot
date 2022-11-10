package com.example.OnlineCourse.service.impl;

import com.example.OnlineCourse.entity.AdminTmp;
import com.example.OnlineCourse.repository.AdminTmpRepository;
import com.example.OnlineCourse.service.AdminTmpService;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminTmpServiceImp implements AdminTmpService {
    private final AdminTmpRepository adminTmpRepository;
    @Override
    public List<AdminTmp> getAll() {
        return adminTmpRepository.findAll(Sort.by("updateAt"));
    }

    @Override
    public AdminTmp findByChatId(Long chatId) {
        return adminTmpRepository.findByChatId(chatId);
    }

    @Override
    public void deleteTmp(Long chatId) {
        adminTmpRepository.delete(findByChatId(chatId));
    }

    @Override
    public AdminTmp saveAdminTmp(String name, String userName, Long chatId) {
        AdminTmp admin=AdminTmp.builder()
                .name(name)
                .userName(userName)
                .chatId(chatId)
                .build();
        adminTmpRepository.save(admin);
        return admin;
    }
}
