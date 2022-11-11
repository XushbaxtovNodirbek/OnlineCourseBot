package com.example.OnlineCourse.service.impl;

import com.example.OnlineCourse.entity.Courses;
import com.example.OnlineCourse.repository.CourseRepository;
import com.example.OnlineCourse.service.CourseService;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j

public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    public List<Courses> getAllCourse() {
        return courseRepository.findAll();
    }

    @Override
    public Courses addCourse(String name, String description) {
        Courses course=Courses.builder()
                .description(description)
                .name(name)
                .build();
        courseRepository.save(course);

        return course;
    }

    @Override
    public Courses findByName(String name) {
        return courseRepository.findByName(name);
    }

    @Override
    public Courses findById(Long id) {
        return courseRepository.findById(id).orElseThrow();
    }

    @Override
    public void delete(Long id) {
        courseRepository.deleteById(id);
    }
}
