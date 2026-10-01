package com.eduquest.controller;

import com.eduquest.domain.TeacherChallenge;
import com.eduquest.domain.UserAccount;
import com.eduquest.service.TeacherChallengeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher/challenges")
public class TeacherChallengeController {

    private final TeacherChallengeService challengeService;

    public TeacherChallengeController(TeacherChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    private UserAccount getUserFromAuth(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof UserAccount user) {
            return user;
        }
        return null;
    }

    private boolean isAdmin(UserAccount user) {
        return user != null && (user.getRole().name().equals("ADMIN") || user.getRole().name().equals("SUPER_ADMIN"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<TeacherChallenge> createChallenge(@RequestBody TeacherChallenge challenge, Authentication auth) {
        UserAccount user = getUserFromAuth(auth);
        if (user != null && challenge.getTeacherId() == null) {
            challenge.setTeacherId(user.getId());
        }
        return ResponseEntity.ok(challengeService.createChallenge(challenge));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<TeacherChallenge> updateChallenge(@PathVariable Long id, @RequestBody TeacherChallenge challenge, Authentication auth) {
        UserAccount user = getUserFromAuth(auth);
        Long teacherId = user != null ? user.getId() : null;
        boolean admin = isAdmin(user);
        return ResponseEntity.ok(challengeService.updateChallenge(id, challenge, teacherId, admin));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteChallenge(@PathVariable Long id, Authentication auth) {
        UserAccount user = getUserFromAuth(auth);
        Long teacherId = user != null ? user.getId() : null;
        boolean admin = isAdmin(user);
        challengeService.deleteChallenge(id, teacherId, admin);
        return ResponseEntity.ok(Map.of("success", true, "message", "Challenge deleted successfully"));
    }

    @PatchMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<TeacherChallenge> archiveChallenge(@PathVariable Long id, Authentication auth) {
        UserAccount user = getUserFromAuth(auth);
        Long teacherId = user != null ? user.getId() : null;
        boolean admin = isAdmin(user);
        return ResponseEntity.ok(challengeService.archiveChallenge(id, teacherId, admin));
    }

    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<TeacherChallenge> restoreChallenge(@PathVariable Long id, Authentication auth) {
        UserAccount user = getUserFromAuth(auth);
        Long teacherId = user != null ? user.getId() : null;
        boolean admin = isAdmin(user);
        return ResponseEntity.ok(challengeService.restoreChallenge(id, teacherId, admin));
    }

    @GetMapping("/classroom/{classroomId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<TeacherChallenge>> getChallengesByClassroom(@PathVariable Long classroomId) {
        return ResponseEntity.ok(challengeService.getChallengesByClassroom(classroomId));
    }

    @GetMapping("/my-challenges")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<TeacherChallenge>> getMyChallenges(Authentication auth) {
        UserAccount user = getUserFromAuth(auth);
        if (user == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(challengeService.getChallengesByTeacher(user.getId()));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getTeacherStats(Authentication auth) {
        UserAccount user = getUserFromAuth(auth);
        if (user == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(challengeService.getTeacherChallengeStats(user.getId()));
    }

    @GetMapping("/admin-stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        return ResponseEntity.ok(challengeService.getAdminChallengeStats());
    }
}
