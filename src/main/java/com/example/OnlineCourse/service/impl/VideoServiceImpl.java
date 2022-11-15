package com.example.OnlineCourse.service.impl;

import com.example.OnlineCourse.entity.Courses;
import com.example.OnlineCourse.entity.Lessons;
import com.example.OnlineCourse.entity.Videos;
import com.example.OnlineCourse.repository.CourseRepository;
import com.example.OnlineCourse.repository.LessonRepository;
import com.example.OnlineCourse.repository.VideoRepository;
import com.example.OnlineCourse.service.LessonService;
import com.example.OnlineCourse.service.VideoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoServiceImpl implements VideoService {
    private final LessonServiceImpl lessonService;
    private final LessonRepository lessonRepository;
    private final VideoRepository videoRepository;
    @Override
    public boolean save(String fileId, String caption, Long lessonId) {
        Lessons lesson=lessonService.findById(lessonId);
        Videos video= Videos.builder()
                .caption(caption)
                .fileId(fileId)
                .build();
        videoRepository.save(video);
        lesson.setVideo(video);
        lessonRepository.save(lesson);
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
