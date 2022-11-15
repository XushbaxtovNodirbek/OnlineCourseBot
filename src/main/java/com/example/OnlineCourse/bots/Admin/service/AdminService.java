package com.example.OnlineCourse.bots.Admin.service;

import com.example.OnlineCourse.entity.Courses;

import java.util.List;

public interface AdminService {
    List<Courses> getAllCourse();
    Courses addCourse(String name);
    boolean deleteCourse(Long id);

}
