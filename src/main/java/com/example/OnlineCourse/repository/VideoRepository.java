package com.example.OnlineCourse.repository;

import com.example.OnlineCourse.entity.Admins;
import com.example.OnlineCourse.entity.Videos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Videos,String > {
}