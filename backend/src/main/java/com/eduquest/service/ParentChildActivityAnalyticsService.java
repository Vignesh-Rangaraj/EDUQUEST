package com.eduquest.service;

import com.eduquest.domain.*;
import com.eduquest.dto.LeaderboardEntryDto;
import com.eduquest.dto.ParentChildActivityAnalyticsDto;
import com.eduquest.dto.ParentSubjectActivityAnalyticsDto;
import com.eduquest.repository.ActivityRepository;
import com.eduquest.repository.StudentProgressRepository;
import com.eduquest.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/** Builds parent-only analytics from the selected child's active classroom activities. */
@Service
public class ParentChildActivityAnalyticsService {
    private static final List<Subject> CURRICULUM_SUBJECTS = List.of(
            Subject.MATHEMATICS, Subject.SCIENCE, Subject.ENGLISH, Subject.SOCIAL_SCIENCE, Subject.TAMIL);

    private final StudentRepository studentRepository;
    private final ActivityRepository activityRepository;
    private final StudentProgressRepository progressRepository;
    private final LeaderboardService leaderboardService;

    public ParentChildActivityAnalyticsService(StudentRepository studentRepository,
            ActivityRepository activityRepository, StudentProgressRepository progressRepository,
            LeaderboardService leaderboardService) {
        this.studentRepository = studentRepository;
        this.activityRepository = activityRepository;
        this.progressRepository = progressRepository;
        this.leaderboardService = leaderboardService;
    }

    @Transactional(readOnly = true)
    public ParentChildActivityAnalyticsDto getAnalytics(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        List<Activity> activeActivities = student.getClassroom() == null
                ? List.of()
                : activityRepository.findPublishedForClassroom(student.getClassroom().getId(), ActivityStatus.PUBLISHED)
                        .stream().filter(Activity::isVisibleToStudents).toList();
        Map<Long, StudentProgress> progressByActivity = progressRepository.findByStudentId(studentId).stream()
                .filter(p -> p.getActivity() != null)
                .collect(Collectors.toMap(p -> p.getActivity().getId(), p -> p, (first, later) -> later));

        List<ParentSubjectActivityAnalyticsDto> subjects = new ArrayList<>();
        for (Subject subject : CURRICULUM_SUBJECTS) {
            List<Activity> subjectActivities = activeActivities.stream()
                    .filter(a -> a.getSubject() == subject).toList();
            subjects.add(summarizeSubject(subject, subjectActivities, progressByActivity));
        }
        // Preserve visibility for any additional subject that actually has published classroom content.
        activeActivities.stream().map(Activity::getSubject).filter(Objects::nonNull)
                .filter(subject -> !CURRICULUM_SUBJECTS.contains(subject)).distinct()
                .forEach(subject -> subjects.add(summarizeSubject(subject,
                        activeActivities.stream().filter(a -> a.getSubject() == subject).toList(), progressByActivity)));

        int completed = (int) activeActivities.stream()
                .filter(a -> Optional.ofNullable(progressByActivity.get(a.getId())).map(StudentProgress::isCompleted).orElse(false))
                .count();
        int inProgress = (int) activeActivities.stream()
                .filter(a -> progressByActivity.containsKey(a.getId()))
                .filter(a -> !progressByActivity.get(a.getId()).isCompleted()).count();
        int active = activeActivities.size();
        int pending = active - completed;
        int percentage = active == 0 ? 0 : Math.round(completed * 100.0f / active);

        List<LeaderboardEntryDto> classroomLeaderboard = student.getClassroom() == null
                ? List.of()
                : leaderboardService.getLeaderboard(studentId, "CLASSROOM");

        return new ParentChildActivityAnalyticsDto(studentId,
                student.getClassroom() == null ? "Unassigned" : student.getClassroom().getName(),
                active, completed, pending, inProgress, percentage, subjects, classroomLeaderboard);
    }

    private ParentSubjectActivityAnalyticsDto summarizeSubject(Subject subject, List<Activity> activities,
            Map<Long, StudentProgress> progressByActivity) {
        int completed = (int) activities.stream()
                .filter(a -> Optional.ofNullable(progressByActivity.get(a.getId())).map(StudentProgress::isCompleted).orElse(false))
                .count();
        int inProgress = (int) activities.stream()
                .filter(a -> progressByActivity.containsKey(a.getId()))
                .filter(a -> !progressByActivity.get(a.getId()).isCompleted()).count();
        int active = activities.size();
        int percentage = active == 0 ? 0 : Math.round(completed * 100.0f / active);
        return new ParentSubjectActivityAnalyticsDto(subject.name(), active, completed, active - completed, inProgress, percentage);
    }
}
