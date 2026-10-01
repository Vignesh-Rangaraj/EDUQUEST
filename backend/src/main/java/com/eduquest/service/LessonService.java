package com.eduquest.service;

import com.eduquest.domain.LessonContent;
import com.eduquest.dto.LessonContentDto;
import com.eduquest.repository.LessonContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LessonService {

    private final LessonContentRepository lessonContentRepository;

    public LessonService(LessonContentRepository lessonContentRepository) {
        this.lessonContentRepository = lessonContentRepository;
    }

    @Transactional(readOnly = true)
    public LessonContentDto getLessonContentByActivityId(Long activityId) {
        LessonContent lc = lessonContentRepository.findByActivityId(activityId).orElse(null);
        return LessonContentDto.fromEntity(lc);
    }

    @Transactional
    public LessonContentDto saveLessonContent(Long activityId, String content, Integer estimatedMinutes) {
        LessonContent lc = lessonContentRepository.findByActivityId(activityId)
                .orElseGet(() -> LessonContent.builder().activityId(activityId).build());

        lc.setContent(content);
        if (estimatedMinutes != null) {
            lc.setEstimatedMinutes(estimatedMinutes);
        }
        lc.setUpdatedAt(LocalDateTime.now());

        LessonContent saved = lessonContentRepository.save(lc);
        return LessonContentDto.fromEntity(saved);
    }
}
