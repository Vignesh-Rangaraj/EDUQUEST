package com.eduquest.service;

import com.eduquest.domain.*;
import com.eduquest.dto.ActivityDto;
import com.eduquest.dto.ContinueLearningDto;
import com.eduquest.dto.CreateActivityRequest;
import com.eduquest.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final StudentRepository studentRepository;
    private final StudentActivityProgressRepository activityProgressRepository;
    private final StudentModuleProgressRepository moduleProgressRepository;
    private final ModuleRepository moduleRepository;
    private final GameConfigurationRepository gameConfigRepository;
    private final StudentProgressRepository studentProgressRepository;
    private final LessonContentRepository lessonContentRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final StudentActivityEventRepository studentActivityEventRepository;
    private final StudentQuizAttemptRepository studentQuizAttemptRepository;
    private final StudentXpTransactionRepository studentXpTransactionRepository;
    private final XpService xpService;
    private final BadgeService badgeService;

    @Value("${app.xp.lesson-completion:10}")
    private int lessonCompletionXp;

    public ActivityService(
            ActivityRepository activityRepository,
            StudentRepository studentRepository,
            StudentActivityProgressRepository activityProgressRepository,
            StudentModuleProgressRepository moduleProgressRepository,
            ModuleRepository moduleRepository,
            GameConfigurationRepository gameConfigRepository,
            StudentProgressRepository studentProgressRepository,
            LessonContentRepository lessonContentRepository,
            QuizQuestionRepository quizQuestionRepository,
            StudentActivityEventRepository studentActivityEventRepository,
            StudentQuizAttemptRepository studentQuizAttemptRepository,
            StudentXpTransactionRepository studentXpTransactionRepository,
            XpService xpService,
            BadgeService badgeService) {
        this.activityRepository = activityRepository;
        this.studentRepository = studentRepository;
        this.activityProgressRepository = activityProgressRepository;
        this.moduleProgressRepository = moduleProgressRepository;
        this.moduleRepository = moduleRepository;
        this.gameConfigRepository = gameConfigRepository;
        this.studentProgressRepository = studentProgressRepository;
        this.lessonContentRepository = lessonContentRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.studentActivityEventRepository = studentActivityEventRepository;
        this.studentQuizAttemptRepository = studentQuizAttemptRepository;
        this.studentXpTransactionRepository = studentXpTransactionRepository;
        this.xpService = xpService;
        this.badgeService = badgeService;
    }

    @Transactional
    public ActivityDto createActivity(CreateActivityRequest request, Long teacherId) {
        int orderIndex = 1;
        if (request.getModuleId() != null) {
            List<Activity> existing = activityRepository.findByModuleIdOrderByDisplayOrderAsc(request.getModuleId());
            orderIndex = existing.size() + 1;
        }

        Subject subject = request.getSubject();
        if (subject == null && request.getModuleId() != null) {
            com.eduquest.domain.Module mod = moduleRepository.findById(request.getModuleId()).orElse(null);
            if (mod != null) subject = mod.getSubject();
        }
        if (subject == null) subject = Subject.MATHEMATICS;

        Activity activity = Activity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .subject(subject)
                .activityType(request.getActivityType() != null ? request.getActivityType() : ActivityType.LESSON)
                .status(ActivityStatus.DRAFT)
                .createdByTeacherId(teacherId)
                .assignedClassroomId(request.getAssignedClassroomId())
                .moduleId(request.getModuleId())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : orderIndex)
                .xpReward(request.getXpReward() != null ? request.getXpReward() : lessonCompletionXp)
                .prerequisiteActivityId(request.getPrerequisiteActivityId())
                .unlockType(request.getUnlockType())
                .unlockValue(request.getUnlockValue())
                .activityMetadataJson(request.getActivityMetadataJson())
                .build();

        Activity saved = activityRepository.save(activity);
        if (request.getActivityMetadataJson() != null && !request.getActivityMetadataJson().trim().isEmpty()) {
            GameConfiguration config = gameConfigRepository.findByActivityId(saved.getId())
                    .orElse(new GameConfiguration());
            config.setActivityId(saved.getId());
            config.setJsonConfiguration(request.getActivityMetadataJson());
            gameConfigRepository.save(config);
        }
        return ActivityDto.fromEntity(saved);
    }

    @Transactional
    public ActivityDto updateActivity(Long id, CreateActivityRequest request, Long teacherId) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found with id: " + id));

        if (teacherId != null && activity.getCreatedByTeacherId() != null && !activity.getCreatedByTeacherId().equals(teacherId)) {
            throw new RuntimeException("Unauthorized: Activity belongs to another teacher");
        }

        activity.setTitle(request.getTitle());
        activity.setDescription(request.getDescription());
        if (request.getSubject() != null) activity.setSubject(request.getSubject());
        if (request.getActivityType() != null) activity.setActivityType(request.getActivityType());
        if (request.getAssignedClassroomId() != null) activity.setAssignedClassroomId(request.getAssignedClassroomId());
        if (request.getModuleId() != null) activity.setModuleId(request.getModuleId());
        if (request.getDisplayOrder() != null) activity.setDisplayOrder(request.getDisplayOrder());
        if (request.getXpReward() != null) activity.setXpReward(request.getXpReward());
        if (request.getPrerequisiteActivityId() != null) activity.setPrerequisiteActivityId(request.getPrerequisiteActivityId());
        if (request.getActivityMetadataJson() != null) activity.setActivityMetadataJson(request.getActivityMetadataJson());
        activity.setUpdatedAt(LocalDateTime.now());

        Activity saved = activityRepository.save(activity);
        if (request.getActivityMetadataJson() != null && !request.getActivityMetadataJson().trim().isEmpty()) {
            GameConfiguration config = gameConfigRepository.findByActivityId(saved.getId())
                    .orElse(new GameConfiguration());
            config.setActivityId(saved.getId());
            config.setJsonConfiguration(request.getActivityMetadataJson());
            gameConfigRepository.save(config);
        }
        return ActivityDto.fromEntity(saved);
    }

    @Transactional
    public ActivityDto archiveActivity(Long id, Long teacherId) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found with id: " + id));

        if (teacherId != null && activity.getCreatedByTeacherId() != null && !activity.getCreatedByTeacherId().equals(teacherId)) {
            throw new RuntimeException("Unauthorized: Activity belongs to another teacher");
        }

        activity.setStatus(ActivityStatus.ARCHIVED);
        activity.setUpdatedAt(LocalDateTime.now());
        Activity saved = activityRepository.save(activity);
        return ActivityDto.fromEntity(saved);
    }

    @Transactional
    public ActivityDto restoreActivity(Long id, Long teacherId) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found with id: " + id));

        if (teacherId != null && activity.getCreatedByTeacherId() != null && !activity.getCreatedByTeacherId().equals(teacherId)) {
            throw new RuntimeException("Unauthorized: Activity belongs to another teacher");
        }

        activity.setStatus(ActivityStatus.PUBLISHED);
        activity.setUpdatedAt(LocalDateTime.now());
        Activity saved = activityRepository.save(activity);
        return ActivityDto.fromEntity(saved);
    }

    @Transactional
    public ActivityDto duplicateActivity(Long id, Long teacherId) {
        Activity original = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found with id: " + id));

        Activity duplicate = Activity.builder()
                .title(original.getTitle() + " (Copy)")
                .description(original.getDescription())
                .subject(original.getSubject())
                .activityType(original.getActivityType())
                .status(ActivityStatus.DRAFT)
                .createdByTeacherId(teacherId != null ? teacherId : original.getCreatedByTeacherId())
                .assignedClassroomId(original.getAssignedClassroomId())
                .moduleId(original.getModuleId())
                .displayOrder((original.getDisplayOrder() != null ? original.getDisplayOrder() : 1) + 1)
                .xpReward(original.getXpReward())
                .build();

        Activity saved = activityRepository.save(duplicate);
        return ActivityDto.fromEntity(saved);
    }

    @Transactional
    public ActivityDto publishActivity(Long id, Long teacherId) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found with id: " + id));

        if (teacherId != null && activity.getCreatedByTeacherId() != null && !activity.getCreatedByTeacherId().equals(teacherId)) {
            throw new RuntimeException("Unauthorized: Activity belongs to another teacher");
        }

        activity.setStatus(ActivityStatus.PUBLISHED);
        activity.setUpdatedAt(LocalDateTime.now());
        Activity saved = activityRepository.save(activity);
        return ActivityDto.fromEntity(saved);
    }

    @Transactional
    public ActivityDto unpublishActivity(Long id, Long teacherId) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found with id: " + id));

        if (teacherId != null && activity.getCreatedByTeacherId() != null && !activity.getCreatedByTeacherId().equals(teacherId)) {
            throw new RuntimeException("Unauthorized: Activity belongs to another teacher");
        }

        activity.setStatus(ActivityStatus.DRAFT);
        activity.setUpdatedAt(LocalDateTime.now());
        Activity saved = activityRepository.save(activity);
        return ActivityDto.fromEntity(saved);
    }

    @Transactional
    public void deleteActivity(Long id, Long teacherId) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found with id: " + id));

        if (teacherId != null && activity.getCreatedByTeacherId() != null && !activity.getCreatedByTeacherId().equals(teacherId)) {
            throw new RuntimeException("Unauthorized: Activity belongs to another teacher");
        }

        // Clear prerequisite references on other activities
        List<Activity> dependentActivities = activityRepository.findByPrerequisiteActivityId(id);
        for (Activity dep : dependentActivities) {
            dep.setPrerequisiteActivityId(null);
            activityRepository.save(dep);
        }

        // Clean up dependent child records
        try { studentProgressRepository.deleteByActivityId(id); } catch (Exception ignored) {}
        try { activityProgressRepository.deleteByActivityId(id); } catch (Exception ignored) {}
        try { lessonContentRepository.deleteByActivityId(id); } catch (Exception ignored) {}
        try { quizQuestionRepository.deleteByActivityId(id); } catch (Exception ignored) {}
        try { studentActivityEventRepository.deleteByActivityId(id); } catch (Exception ignored) {}
        try { studentQuizAttemptRepository.deleteByActivityId(id); } catch (Exception ignored) {}
        try { studentXpTransactionRepository.deleteByActivityId(id); } catch (Exception ignored) {}
        try { gameConfigRepository.deleteByActivityId(id); } catch (Exception ignored) {}

        activityRepository.delete(activity);
    }

    @Transactional(readOnly = true)
    public List<ActivityDto> getLessonsForModule(Long moduleId, Long studentId) {
        List<Activity> activities = activityRepository.findByModuleIdAndStatusOrderByDisplayOrderAsc(moduleId, ActivityStatus.PUBLISHED);
        if (studentId == null) {
            return activities.stream().map(ActivityDto::fromEntity).collect(Collectors.toList());
        }

        List<StudentActivityProgress> studentProgresses = activityProgressRepository.findByStudentId(studentId);
        List<ActivityDto> dtos = new ArrayList<>();

        boolean foundCurrent = false;

        for (int i = 0; i < activities.size(); i++) {
            Activity act = activities.get(i);
            ActivityDto dto = ActivityDto.fromEntity(act);

            Optional<StudentActivityProgress> progOpt = studentProgresses.stream()
                    .filter(p -> p.getActivityId().equals(act.getId()))
                    .findFirst();

            boolean isCompleted = progOpt.isPresent() && progOpt.get().isCompleted();
            dto.setCompleted(isCompleted);

            if (isCompleted) {
                dto.setStatusBadge("COMPLETED"); // ✓ Completed
            } else if (!foundCurrent) {
                Long prevActivityId = i > 0 ? activities.get(i - 1).getId() : null;
                boolean prevCompleted = (i == 0) || (prevActivityId != null && studentProgresses.stream().anyMatch(p -> p.getActivityId().equals(prevActivityId) && p.isCompleted()));
                if (prevCompleted) {
                    dto.setStatusBadge("CURRENT"); // ▶ Current
                    foundCurrent = true;
                } else {
                    dto.setStatusBadge("LOCKED"); // 🔒 Locked
                }
            } else {
                dto.setStatusBadge("LOCKED"); // 🔒 Locked
            }

            dtos.add(dto);
        }

        return dtos;
    }

    @Transactional
    public ActivityDto completeLesson(Long studentId, Long lessonId) {
        Activity activity = activityRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson/Activity not found with id: " + lessonId));

        // Step 1: Mark lesson completed in StudentActivityProgress
        Optional<StudentActivityProgress> existingProg = activityProgressRepository.findByStudentIdAndActivityId(studentId, lessonId);
        StudentActivityProgress progress;
        if (existingProg.isPresent()) {
            progress = existingProg.get();
            progress.setCompleted(true);
            progress.setScore(100);
            if (progress.getCompletedAt() == null) {
                progress.setCompletedAt(LocalDateTime.now());
            }
        } else {
            progress = StudentActivityProgress.builder()
                    .studentId(studentId)
                    .activityId(lessonId)
                    .completed(true)
                    .score(100)
                    .bestScore(100)
                    .completedAt(LocalDateTime.now())
                    .build();
        }
        activityProgressRepository.save(progress);

        // Step 2: Award XP using configurable property (Idempotent)
        int xpReward = activity.getXpReward() != null ? activity.getXpReward() : lessonCompletionXp;
        xpService.awardXp(studentId, lessonId, xpReward, "LESSON_COMPLETION");

        // Step 3: Update StudentModuleProgress (completionPercentage = completedActivities / totalActivities * 100)
        if (activity.getModuleId() != null) {
            Long moduleId = activity.getModuleId();
            List<Activity> moduleActivities = activityRepository.findByModuleIdAndStatusOrderByDisplayOrderAsc(moduleId, ActivityStatus.PUBLISHED);
            int totalActivities = moduleActivities.size();

            long completedCount = moduleActivities.stream()
                    .filter(a -> activityProgressRepository.findByStudentIdAndActivityId(studentId, a.getId())
                            .map(StudentActivityProgress::isCompleted)
                            .orElse(false))
                    .count();

            double percentage = totalActivities > 0 ? (completedCount * 100.0) / totalActivities : 0.0;
            boolean isModuleCompleted = completedCount >= totalActivities;

            Optional<StudentModuleProgress> modProgOpt = moduleProgressRepository.findByStudentIdAndModuleId(studentId, moduleId);
            StudentModuleProgress modProg;
            if (modProgOpt.isPresent()) {
                modProg = modProgOpt.get();
                modProg.setCompletedActivities((int) completedCount);
                modProg.setTotalActivities(totalActivities);
                modProg.setCompletionPercentage(percentage);
                if (isModuleCompleted) {
                    modProg.setCompleted(true);
                    if (modProg.getCompletedAt() == null) modProg.setCompletedAt(LocalDateTime.now());
                }
            } else {
                modProg = StudentModuleProgress.builder()
                        .studentId(studentId)
                        .moduleId(moduleId)
                        .completedActivities((int) completedCount)
                        .totalActivities(totalActivities)
                        .completionPercentage(percentage)
                        .completed(isModuleCompleted)
                        .completedAt(isModuleCompleted ? LocalDateTime.now() : null)
                        .build();
            }
            moduleProgressRepository.save(modProg);
        }

        // Step 4 & 5: Evaluate achievements & badges
        badgeService.evaluateBadges(studentId);

        ActivityDto dto = ActivityDto.fromEntity(activity);
        dto.setCompleted(true);
        dto.setStatusBadge("COMPLETED");
        return dto;
    }

    @Transactional(readOnly = true)
    public ContinueLearningDto getContinueLearning(Long studentId) {
        List<StudentActivityProgress> allProgress = activityProgressRepository.findByStudentId(studentId);

        // Find last completed or touched activity
        Optional<StudentActivityProgress> lastProgress = allProgress.stream()
                .sorted(Comparator.comparing(StudentActivityProgress::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .findFirst();

        if (lastProgress.isEmpty()) {
            // Default to first published module and first lesson
            List<Activity> published = activityRepository.findByStatus(ActivityStatus.PUBLISHED);
            if (!published.isEmpty()) {
                Activity first = published.get(0);
                com.eduquest.domain.Module mod = first.getModuleId() != null ? moduleRepository.findById(first.getModuleId()).orElse(null) : null;
                return new ContinueLearningDto(
                        first.getModuleId(),
                        mod != null ? mod.getTitle() : "Introductory Topic",
                        first.getId(),
                        first.getTitle(),
                        0.0,
                        first.getId()
                );
            }
            return new ContinueLearningDto(null, "No Active Modules", null, "No Lessons", 0.0, null);
        }

        Activity lastActivity = activityRepository.findById(lastProgress.get().getActivityId()).orElse(null);
        if (lastActivity == null) {
            return new ContinueLearningDto(null, "No Active Modules", null, "No Lessons", 0.0, null);
        }

        Long moduleId = lastActivity.getModuleId();
        com.eduquest.domain.Module module = moduleId != null ? moduleRepository.findById(moduleId).orElse(null) : null;

        List<Activity> moduleLessons = moduleId != null ? activityRepository.findByModuleIdAndStatusOrderByDisplayOrderAsc(moduleId, ActivityStatus.PUBLISHED) : List.of();

        Long nextLessonId = lastActivity.getId();
        for (int i = 0; i < moduleLessons.size(); i++) {
            if (moduleLessons.get(i).getId().equals(lastActivity.getId()) && i + 1 < moduleLessons.size()) {
                nextLessonId = moduleLessons.get(i + 1).getId();
                break;
            }
        }

        double modPct = 0.0;
        if (moduleId != null) {
            Optional<StudentModuleProgress> modProg = moduleProgressRepository.findByStudentIdAndModuleId(studentId, moduleId);
            if (modProg.isPresent()) {
                modPct = modProg.get().getCompletionPercentage();
            }
        }

        return new ContinueLearningDto(
                moduleId,
                module != null ? module.getTitle() : "Current Module",
                lastActivity.getId(),
                lastActivity.getTitle(),
                modPct,
                nextLessonId
        );
    }

    @Transactional(readOnly = true)
    public List<ActivityDto> getActivitiesByTeacher(Long teacherId) {
        return activityRepository.findByCreatedByTeacherId(teacherId).stream()
                .map(ActivityDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActivityDto> getPublishedActivitiesForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        Long classroomId = (student != null && student.getClassroom() != null) ? student.getClassroom().getId() : null;
        if (classroomId != null) {
            return activityRepository.findPublishedForClassroom(classroomId, ActivityStatus.PUBLISHED).stream()
                    .map(ActivityDto::fromEntity)
                    .collect(Collectors.toList());
        } else {
            return activityRepository.findByStatus(ActivityStatus.PUBLISHED).stream()
                    .map(ActivityDto::fromEntity)
                    .collect(Collectors.toList());
        }
    }
}
