package com.eduquest.service;

import com.eduquest.domain.Activity;
import com.eduquest.domain.StudentActivityProgress;
import com.eduquest.domain.StudentModuleProgress;
import com.eduquest.dto.StudentModuleProgressDto;
import com.eduquest.repository.ActivityRepository;
import com.eduquest.repository.ModuleRepository;
import com.eduquest.repository.StudentActivityProgressRepository;
import com.eduquest.repository.StudentModuleProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentModuleProgressService {

    private final StudentModuleProgressRepository moduleProgressRepository;
    private final ActivityRepository activityRepository;
    private final StudentActivityProgressRepository activityProgressRepository;
    private final ModuleRepository moduleRepository;

    public StudentModuleProgressService(
            StudentModuleProgressRepository moduleProgressRepository,
            ActivityRepository activityRepository,
            StudentActivityProgressRepository activityProgressRepository,
            ModuleRepository moduleRepository) {
        this.moduleProgressRepository = moduleProgressRepository;
        this.activityRepository = activityRepository;
        this.activityProgressRepository = activityProgressRepository;
        this.moduleRepository = moduleRepository;
    }

    @Transactional
    public StudentModuleProgress updateModuleProgress(Long studentId, Long moduleId) {
        if (studentId == null || moduleId == null) return null;

        List<Activity> moduleActivities = activityRepository.findAll().stream()
                .filter(a -> moduleId.equals(a.getModuleId()))
                .collect(Collectors.toList());

        int totalActivities = moduleActivities.size();
        if (totalActivities == 0) return null;

        Set<Long> activityIds = moduleActivities.stream().map(Activity::getId).collect(Collectors.toSet());
        List<StudentActivityProgress> studentProgresses = activityProgressRepository.findByStudentId(studentId);

        int completedCount = (int) studentProgresses.stream()
                .filter(p -> activityIds.contains(p.getActivityId()) && p.isCompleted())
                .count();

        double percentage = Math.min(100.0, Math.round(((double) completedCount / totalActivities) * 100.0));
        boolean isModuleCompleted = completedCount >= totalActivities;

        StudentModuleProgress smp = moduleProgressRepository.findByStudentIdAndModuleId(studentId, moduleId)
                .orElseGet(() -> StudentModuleProgress.builder().studentId(studentId).moduleId(moduleId).build());

        smp.setCompletedActivities(completedCount);
        smp.setTotalActivities(totalActivities);
        smp.setCompletionPercentage(percentage);
        smp.setCompleted(isModuleCompleted);
        if (isModuleCompleted && smp.getCompletedAt() == null) {
            smp.setCompletedAt(LocalDateTime.now());
        }

        return moduleProgressRepository.save(smp);
    }

    @Transactional(readOnly = true)
    public List<StudentModuleProgressDto> getModuleProgressForStudent(Long studentId) {
        List<StudentModuleProgress> progresses = moduleProgressRepository.findByStudentId(studentId);
        Map<Long, String> moduleTitleMap = moduleRepository.findAll().stream()
                .collect(Collectors.toMap(com.eduquest.domain.Module::getId, com.eduquest.domain.Module::getTitle, (a, b) -> a));

        return progresses.stream()
                .map(p -> StudentModuleProgressDto.fromEntity(p, moduleTitleMap.getOrDefault(p.getModuleId(), "Module #" + p.getModuleId())))
                .collect(Collectors.toList());
    }
}
