package com.eduquest.service;

import com.eduquest.domain.*;
import com.eduquest.dto.AdminAnalyticsDto;
import com.eduquest.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminAnalyticsService {

    private final SchoolRepository schoolRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final ActivityRepository activityRepository;
    private final ClassroomRepository classroomRepository;
    private final ModuleRepository moduleRepository;

    public AdminAnalyticsService(
            SchoolRepository schoolRepository,
            TeacherRepository teacherRepository,
            StudentRepository studentRepository,
            ActivityRepository activityRepository,
            ClassroomRepository classroomRepository,
            ModuleRepository moduleRepository) {
        this.schoolRepository = schoolRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.activityRepository = activityRepository;
        this.classroomRepository = classroomRepository;
        this.moduleRepository = moduleRepository;
    }

    @Transactional(readOnly = true)
    public AdminAnalyticsDto getAdminAnalytics() {
        long totalSchools = schoolRepository.count();
        long totalTeachers = teacherRepository.count();
        long totalStudents = studentRepository.count();

        long totalModules = moduleRepository.count();
        long publishedModules = moduleRepository.findByStatus(ActivityStatus.PUBLISHED).size();

        List<Activity> allActivities = activityRepository.findAll();
        long totalLessons = allActivities.stream().filter(a -> a.getActivityType() == ActivityType.LESSON).count();
        long publishedLessons = allActivities.stream().filter(a -> a.getActivityType() == ActivityType.LESSON && a.getStatus() == ActivityStatus.PUBLISHED).count();
        long totalQuizzes = allActivities.stream().filter(a -> a.getActivityType() == ActivityType.QUIZ).count();

        List<Student> allStudents = studentRepository.findAll();
        long activeStudents = allStudents.stream().filter(s -> s.getXp() != null && s.getXp() > 0).count();
        int activeRate = totalStudents > 0 ? (int) ((activeStudents * 100) / totalStudents) : 0;

        // Top Classrooms
        List<Classroom> classrooms = classroomRepository.findAll();
        List<AdminAnalyticsDto.ClassroomSummaryDto> topClassrooms = new ArrayList<>();
        for (Classroom c : classrooms) {
            List<Student> classroomStudents = studentRepository.findByClassroom_Id(c.getId());
            int count = classroomStudents.size();
            int avgXp = count > 0 ? (int) classroomStudents.stream().mapToInt(s -> s.getXp() != null ? s.getXp() : 0).average().orElse(0) : 0;

            topClassrooms.add(new AdminAnalyticsDto.ClassroomSummaryDto(
                    c.getId(),
                    c.getName(),
                    c.getGrade() != null ? "Grade " + c.getGrade() : "N/A",
                    count,
                    avgXp
            ));
        }

        topClassrooms.sort(Comparator.comparingInt(AdminAnalyticsDto.ClassroomSummaryDto::getAverageXp).reversed());
        if (topClassrooms.size() > 5) {
            topClassrooms = topClassrooms.subList(0, 5);
        }

        // Top Students
        List<AdminAnalyticsDto.StudentLeaderboardSummaryDto> topStudents = allStudents.stream()
                .sorted(Comparator.comparingInt((Student s) -> s.getXp() != null ? s.getXp() : 0).reversed())
                .limit(5)
                .map(s -> new AdminAnalyticsDto.StudentLeaderboardSummaryDto(
                        s.getId(),
                        s.getUserAccount() != null ? s.getUserAccount().getFullName() : "Student #" + s.getId(),
                        s.getClassroom() != null ? s.getClassroom().getName() : "Unassigned",
                        s.getXp() != null ? s.getXp() : 0,
                        s.getLevel() != null ? s.getLevel() : 1
                ))
                .collect(Collectors.toList());

        // Top Teachers
        List<Teacher> teachers = teacherRepository.findAll();
        List<AdminAnalyticsDto.TeacherSummaryDto> topTeachers = teachers.stream()
                .limit(5)
                .map(t -> new AdminAnalyticsDto.TeacherSummaryDto(
                        t.getId(),
                        t.getUserAccount() != null ? t.getUserAccount().getFullName() : "Teacher #" + t.getId(),
                        t.getClassroom() != null ? t.getClassroom().getName() : "Unassigned",
                        "GENERAL"
                ))
                .collect(Collectors.toList());

        return new AdminAnalyticsDto(
                totalSchools,
                totalTeachers,
                totalStudents,
                totalModules,
                publishedModules,
                totalLessons,
                publishedLessons,
                totalQuizzes,
                activeRate,
                topClassrooms,
                topStudents,
                topTeachers
        );
    }
}
