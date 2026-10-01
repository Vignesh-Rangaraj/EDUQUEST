package com.eduquest.controller;

import com.eduquest.domain.Teacher;
import com.eduquest.domain.UserAccount;
import com.eduquest.dto.ActivityDto;
import com.eduquest.dto.CreateActivityRequest;
import com.eduquest.repository.TeacherRepository;
import com.eduquest.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
public class TeacherActivityController {

    private final ActivityService activityService;
    private final TeacherRepository teacherRepository;

    public TeacherActivityController(ActivityService activityService, TeacherRepository teacherRepository) {
        this.activityService = activityService;
        this.teacherRepository = teacherRepository;
    }

    private Teacher getTeacher(Authentication authentication) {
        UserAccount user = (UserAccount) authentication.getPrincipal();
        return teacherRepository.findByUserAccountUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Teacher profile not found for user: " + user.getUsername()));
    }

    @GetMapping("/activities")
    public ResponseEntity<List<ActivityDto>> getMyActivities(Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        return ResponseEntity.ok(activityService.getActivitiesByTeacher(teacher.getId()));
    }

    @PostMapping("/activities")
    public ResponseEntity<ActivityDto> createActivity(@RequestBody CreateActivityRequest request, Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        if (request.getAssignedClassroomId() == null && teacher.getClassroom() != null) {
            request.setAssignedClassroomId(teacher.getClassroom().getId());
        }
        return ResponseEntity.ok(activityService.createActivity(request, teacher.getId()));
    }

    @PutMapping("/activities/{id}")
    public ResponseEntity<ActivityDto> updateActivity(@PathVariable Long id, @RequestBody CreateActivityRequest request, Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        return ResponseEntity.ok(activityService.updateActivity(id, request, teacher.getId()));
    }

    @DeleteMapping("/activities/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id, Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        activityService.deleteActivity(id, teacher.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/activities/{id}/publish")
    public ResponseEntity<ActivityDto> publishActivity(@PathVariable Long id, Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        return ResponseEntity.ok(activityService.publishActivity(id, teacher.getId()));
    }

    @PutMapping("/activities/{id}/publish")
    public ResponseEntity<ActivityDto> publishActivityPut(@PathVariable Long id, Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        return ResponseEntity.ok(activityService.publishActivity(id, teacher.getId()));
    }

    @PostMapping("/activities/{id}/unpublish")
    public ResponseEntity<ActivityDto> unpublishActivity(@PathVariable Long id, Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        return ResponseEntity.ok(activityService.unpublishActivity(id, teacher.getId()));
    }

    @PostMapping("/activities/{id}/archive")
    public ResponseEntity<ActivityDto> archiveActivity(@PathVariable Long id, Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        return ResponseEntity.ok(activityService.archiveActivity(id, teacher.getId()));
    }

    @PutMapping("/activities/{id}/archive")
    public ResponseEntity<ActivityDto> archiveActivityPut(@PathVariable Long id, Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        return ResponseEntity.ok(activityService.archiveActivity(id, teacher.getId()));
    }

    @PostMapping("/activities/{id}/restore")
    public ResponseEntity<ActivityDto> restoreActivity(@PathVariable Long id, Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        return ResponseEntity.ok(activityService.restoreActivity(id, teacher.getId()));
    }

    @PutMapping("/activities/{id}/restore")
    public ResponseEntity<ActivityDto> restoreActivityPut(@PathVariable Long id, Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        return ResponseEntity.ok(activityService.restoreActivity(id, teacher.getId()));
    }

    @PostMapping("/activities/{id}/duplicate")
    public ResponseEntity<ActivityDto> duplicateActivity(@PathVariable Long id, Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        return ResponseEntity.ok(activityService.duplicateActivity(id, teacher.getId()));
    }
}
