package com.example.OnlineCourse.repository;

import com.example.OnlineCourse.entity.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Courses,Long> {
    Courses findByName(String name);
}
