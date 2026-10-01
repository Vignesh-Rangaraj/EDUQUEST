package com.eduquest.service;

import com.eduquest.domain.Student;
import com.eduquest.dto.LeaderboardEntryDto;
import com.eduquest.repository.StudentActivityProgressRepository;
import com.eduquest.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LeaderboardService {

    private final StudentRepository studentRepository;
    private final StudentActivityProgressRepository progressRepository;

    public LeaderboardService(StudentRepository studentRepository, StudentActivityProgressRepository progressRepository) {
        this.studentRepository = studentRepository;
        this.progressRepository = progressRepository;
    }

    // Phase 4 Backward Compatibility Method
    public List<LeaderboardEntryDto> getLeaderboard(Long studentId, String scope) {
        Student currentStudent = studentId != null ? studentRepository.findById(studentId).orElse(null) : null;
        List<Student> students;

        if ("CLASSROOM".equalsIgnoreCase(scope) && currentStudent != null && currentStudent.getClassroom() != null) {
            students = studentRepository.findByClassroomId(currentStudent.getClassroom().getId());
        } else {
            students = studentRepository.findAll();
        }

        students.sort((a, b) -> Integer.compare(
                b.getXp() != null ? b.getXp() : 0,
                a.getXp() != null ? a.getXp() : 0
        ));

        List<LeaderboardEntryDto> list = new ArrayList<>();
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            String name = s.getUserAccount() != null ? s.getUserAccount().getFullName() : "Student #" + s.getId();
            String className = s.getClassroom() != null ? (s.getClassroom().getName() != null ? s.getClassroom().getName() : "Grade " + s.getClassroom().getGrade() + " - " + s.getClassroom().getSection()) : "Unassigned";

            list.add(new LeaderboardEntryDto(
                    i + 1,
                    s.getId(),
                    name,
                    className,
                    s.getXp() != null ? s.getXp() : 0,
                    s.getLevel() != null ? s.getLevel() : 1
            ));
        }

        return list;
    }

    // Phase 7 Multi-Scope & Multi-Metric Method
    public List<Map<String, Object>> getLeaderboard(String scope, Long scopeId, String metric) {
        List<Student> students;

        if ("CLASSROOM".equalsIgnoreCase(scope) && scopeId != null) {
            students = studentRepository.findByClassroomId(scopeId);
        } else {
            students = studentRepository.findAll();
        }

        List<Map<String, Object>> rankedList = new ArrayList<>();

        for (Student s : students) {
            Map<String, Object> item = new HashMap<>();
            item.put("studentId", s.getId());
            item.put("name", s.getUserAccount() != null ? s.getUserAccount().getFullName() : "Student #" + s.getId());
            item.put("classroomName", s.getClassroom() != null ? (s.getClassroom().getName() != null ? s.getClassroom().getName() : "Grade " + s.getClassroom().getGrade() + " - " + s.getClassroom().getSection()) : "Unassigned");
            item.put("level", s.getLevel() != null ? s.getLevel() : 1);
            item.put("xp", s.getXp() != null ? s.getXp() : 0);
            item.put("coins", s.getCoins() != null ? s.getCoins() : 0);
            item.put("streak", s.getCurrentStreak() != null ? s.getCurrentStreak() : 0);

            long val = 0;
            if ("LESSONS_COMPLETED".equalsIgnoreCase(metric)) {
                val = progressRepository.countByStudentIdAndCompletedTrue(s.getId());
            } else if ("STREAKS".equalsIgnoreCase(metric)) {
                val = s.getCurrentStreak() != null ? s.getCurrentStreak() : 0;
            } else {
                val = s.getXp() != null ? s.getXp() : 0;
            }

            item.put("metricValue", val);
            rankedList.add(item);
        }

        rankedList.sort((a, b) -> {
            long vA = ((Number) a.get("metricValue")).longValue();
            long vB = ((Number) b.get("metricValue")).longValue();
            if (vA != vB) return Long.compare(vB, vA);
            int xpA = (Integer) a.get("xp");
            int xpB = (Integer) b.get("xp");
            return Integer.compare(xpB, xpA);
        });

        for (int i = 0; i < rankedList.size(); i++) {
            rankedList.get(i).put("rank", i + 1);
        }

        return rankedList;
    }
}
