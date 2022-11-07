package com.example.OnlineCourse.repository;

import com.example.OnlineCourse.entity.SecurityKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyRepository extends JpaRepository<SecurityKey,String> {

}
