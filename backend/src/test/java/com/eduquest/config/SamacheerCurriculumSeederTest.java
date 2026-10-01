package com.eduquest.config;

import com.eduquest.domain.ActivityStatus;
import com.eduquest.domain.ActivityType;
import com.eduquest.dto.ActivityDto;
import com.eduquest.dto.ModuleDto;
import com.eduquest.domain.Student;
import com.eduquest.repository.StudentRepository;
import com.eduquest.service.ModuleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class SamacheerCurriculumSeederTest {

    @Autowired
    private DataInitializer dataInitializer;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private com.eduquest.repository.ActivityRepository activityRepository;

    @Test
    public void testCurriculumSeedingAndStudentVisibilityForClasses6To9() {
        // Run initializer
        dataInitializer.run();

        // 1. Verify Class 6
        Student student6 = studentRepository.findByUserAccountUsername("student_6a_1").orElseThrow();
        List<ModuleDto> modules6 = moduleService.getPublishedModulesForStudent(student6.getId());
        assertEquals(4, modules6.size(), "Class 6 should have exactly 4 published modules");

        long lessons6Count = modules6.stream()
                .flatMap(m -> activityRepository.findByModuleIdOrderByDisplayOrderAsc(m.getId()).stream())
                .filter(a -> a.getActivityType() == ActivityType.LESSON)
                .count();
        assertEquals(4, lessons6Count, "Class 6 should have 4 lessons");

        long games6Count = modules6.stream()
                .flatMap(m -> activityRepository.findByModuleIdOrderByDisplayOrderAsc(m.getId()).stream())
                .filter(a -> a.getActivityType() != ActivityType.LESSON)
                .count();
        assertEquals(4, games6Count, "Class 6 should have 4 games");

        // 2. Verify Class 7
        Student student7 = studentRepository.findByUserAccountUsername("student_7a_1").orElseThrow();
        List<ModuleDto> modules7 = moduleService.getPublishedModulesForStudent(student7.getId());
        assertEquals(4, modules7.size(), "Class 7 should have exactly 4 published modules");

        long lessons7Count = modules7.stream()
                .flatMap(m -> activityRepository.findByModuleIdOrderByDisplayOrderAsc(m.getId()).stream())
                .filter(a -> a.getActivityType() == ActivityType.LESSON)
                .count();
        assertEquals(4, lessons7Count, "Class 7 should have 4 lessons");

        long games7Count = modules7.stream()
                .flatMap(m -> activityRepository.findByModuleIdOrderByDisplayOrderAsc(m.getId()).stream())
                .filter(a -> a.getActivityType() != ActivityType.LESSON)
                .count();
        assertEquals(4, games7Count, "Class 7 should have 4 games");

        // 3. Verify Class 8
        Student student8 = studentRepository.findByUserAccountUsername("student_8a_1").orElseThrow();
        List<ModuleDto> modules8 = moduleService.getPublishedModulesForStudent(student8.getId());
        assertEquals(4, modules8.size(), "Class 8 should have exactly 4 published modules");

        long lessons8Count = modules8.stream()
                .flatMap(m -> activityRepository.findByModuleIdOrderByDisplayOrderAsc(m.getId()).stream())
                .filter(a -> a.getActivityType() == ActivityType.LESSON)
                .count();
        assertEquals(4, lessons8Count, "Class 8 should have 4 lessons");

        long games8Count = modules8.stream()
                .flatMap(m -> activityRepository.findByModuleIdOrderByDisplayOrderAsc(m.getId()).stream())
                .filter(a -> a.getActivityType() != ActivityType.LESSON)
                .count();
        assertEquals(4, games8Count, "Class 8 should have 4 games");

        // 4. Verify Class 9
        Student student9 = studentRepository.findByUserAccountUsername("student_9a_1").orElseThrow();
        List<ModuleDto> modules9 = moduleService.getPublishedModulesForStudent(student9.getId());
        assertEquals(4, modules9.size(), "Class 9 should have exactly 4 published modules");

        long lessons9Count = modules9.stream()
                .flatMap(m -> activityRepository.findByModuleIdOrderByDisplayOrderAsc(m.getId()).stream())
                .filter(a -> a.getActivityType() == ActivityType.LESSON)
                .count();
        assertEquals(4, lessons9Count, "Class 9 should have 4 lessons");

        long games9Count = modules9.stream()
                .flatMap(m -> activityRepository.findByModuleIdOrderByDisplayOrderAsc(m.getId()).stream())
                .filter(a -> a.getActivityType() != ActivityType.LESSON)
                .count();
        assertEquals(4, games9Count, "Class 9 should have 4 games");
    }
}
