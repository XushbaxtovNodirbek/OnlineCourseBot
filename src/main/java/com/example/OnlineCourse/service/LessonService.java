package com.example.OnlineCourse.service;

import com.example.OnlineCourse.entity.Lessons;

public interface LessonService {
    boolean deleteLesson(Long id);
    Lessons addLesson(String name);
    Lessons findById(Long id);
}
