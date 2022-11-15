package com.example.OnlineCourse.service.impl;

import com.example.OnlineCourse.entity.Lessons;
import com.example.OnlineCourse.repository.LessonRepository;
import com.example.OnlineCourse.service.LessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;

    @Override
    public boolean deleteLesson(Long id) {
        try {
            lessonRepository.deleteById(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public Lessons addLesson(String name) {
        Lessons lessons=Lessons.builder()
                .name(name)
                .build();
        lessonRepository.save(lessons);
        return lessons;
    }

    @Override
    public Lessons findById(Long id) {
        try {
            return lessonRepository.findById(id).orElseThrow();
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }
}
