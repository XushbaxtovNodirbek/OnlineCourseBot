package com.example.OnlineCourse.service;

import com.example.OnlineCourse.repository.KeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeyService {
    private final KeyRepository keyRepository;

}
