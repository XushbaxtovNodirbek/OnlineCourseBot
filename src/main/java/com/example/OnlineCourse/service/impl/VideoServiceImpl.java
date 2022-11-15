package com.example.OnlineCourse.service.impl;

import com.example.OnlineCourse.entity.Courses;
import com.example.OnlineCourse.entity.Videos;
import com.example.OnlineCourse.repository.CourseRepository;
import com.example.OnlineCourse.repository.VideoRepository;
import com.example.OnlineCourse.service.VideoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoServiceImpl implements VideoService {
    private final CourseRepository courseRepository;
    private final VideoRepository videoRepository;
    @Override
    public boolean save(String fileId, String caption, Long courseId) {
        Courses course=courseRepository.getById(courseId);
        List<Videos> videosList = course.getVideosList();
        Videos video=Videos.builder()
                .fileId(fileId)
                .caption(caption)
                .build();
        videoRepository.save(video);
        videosList.add(video);
        course.setVideosList(videosList);
        courseRepository.save(course);
        return true;
    }

    @Override
    public boolean delete(Videos videos) {
        try {
            videoRepository.delete(videos);
            return true;
        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }
}
