package com.example.OnlineCourse.bots.Admin.service;

import com.example.OnlineCourse.entity.AdminTmp;
import com.example.OnlineCourse.entity.User;

import java.util.List;

public interface OwnerService {
    User getAdminById(Long id);
    List<User> getAllAdmins();
    User addAdmin(AdminTmp adminTmp);
    boolean deleteAdmin(Long id);
    boolean deleteAdminTmp(Long id);
}
