package com.example.OnlineCourse.repository;

import com.example.OnlineCourse.entity.Lessons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lessons,Long> {

}
