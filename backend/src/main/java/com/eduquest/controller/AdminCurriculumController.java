package com.eduquest.controller;

import com.eduquest.config.DataInitializer;
import com.eduquest.domain.ActivityStatus;
import com.eduquest.domain.Subject;
import com.eduquest.dto.CurriculumOverviewDto;
import com.eduquest.dto.ModuleDto;
import com.eduquest.repository.ActivityRepository;
import com.eduquest.repository.ModuleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/curriculum")
public class AdminCurriculumController {

    private final ModuleRepository moduleRepository;
    private final ActivityRepository activityRepository;
    private final DataInitializer dataInitializer;

    public AdminCurriculumController(
            ModuleRepository moduleRepository,
            ActivityRepository activityRepository,
            DataInitializer dataInitializer) {
        this.moduleRepository = moduleRepository;
        this.activityRepository = activityRepository;
        this.dataInitializer = dataInitializer;
    }

    @GetMapping
    public ResponseEntity<CurriculumOverviewDto> getCurriculumOverview() {
        long totalSubjects = Subject.values().length;
        long totalModules = moduleRepository.count();
        long totalActivities = activityRepository.count();
        long publishedModules = moduleRepository.findByStatus(ActivityStatus.PUBLISHED).size();
        long publishedActivities = activityRepository.findByStatus(ActivityStatus.PUBLISHED).size();

        List<ModuleDto> modules = moduleRepository.findAll().stream()
                .map(ModuleDto::fromEntity)
                .collect(Collectors.toList());

        CurriculumOverviewDto overview = new CurriculumOverviewDto(
                totalSubjects,
                totalModules,
                totalActivities,
                publishedModules,
                publishedActivities,
                modules
        );

        return ResponseEntity.ok(overview);
    }

    @PostMapping("/seed-demo")
    public ResponseEntity<Map<String, Object>> seedDemoCurriculum() {
        dataInitializer.seedDemoCurriculumOnDemand();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Demo curriculum loaded on demand successfully.");
        response.put("totalModules", moduleRepository.count());
        response.put("totalActivities", activityRepository.count());
        return ResponseEntity.ok(response);
    }
}
