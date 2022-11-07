package com.example.OnlineCourse.service;

import com.example.OnlineCourse.entity.Admins;
import com.example.OnlineCourse.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final AdminRepository adminRepository;

    public Admins saveAdmin(Long chatId,String name,String isActive){
        try{
            Admins admin = Admins.builder()
                    .chatId(chatId)
                    .name(name)
                    .isActive(isActive)
                    .step("START")
                    .build();
            adminRepository.save(admin);
            return admin;
        }catch (Exception e){
            return null;
        }

    }
    public Admins findByChatId(Long chatId){
        return adminRepository.findAdminsByChatId(chatId);
    }
    public Admins changeActive(Long chatId,boolean isActive){
        Admins admin=findByChatId(chatId);
        return admin;
    }
    public Admins changeStep(Long chatId,String step){
        Admins admins=findByChatId(chatId);
        admins.setStep(step);
        return admins;
    }

}
