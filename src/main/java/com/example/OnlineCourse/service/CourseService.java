package com.example.OnlineCourse.service;

import com.example.OnlineCourse.entity.Courses;

import java.util.List;

public interface CourseService {
    List<Courses> getAllCourse();
    Courses addCourse(String name,String  description);
    Courses findByName(String name);
    Courses findById(Long id);
    void delete(Long id);
}
