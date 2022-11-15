package com.example.OnlineCourse.service;

import com.example.OnlineCourse.entity.Videos;

public interface VideoService {
    boolean save(String fileId,String caption ,Long courseId);
    boolean delete(Videos videos);
}
