package com.eduquest.controller;

import com.eduquest.domain.Student;
import com.eduquest.domain.UserAccount;
import com.eduquest.dto.SyncRequestDto;
import com.eduquest.dto.SyncResponse;
import com.eduquest.repository.StudentRepository;
import com.eduquest.service.SyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/student/sync")
public class StudentSyncController {

    private final SyncService syncService;
    private final StudentRepository studentRepository;

    public StudentSyncController(SyncService syncService, StudentRepository studentRepository) {
        this.syncService = syncService;
        this.studentRepository = studentRepository;
    }

    @PostMapping
    public ResponseEntity<SyncResponse> processSync(@RequestBody SyncRequestDto request, Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserAccount) {
            UserAccount user = (UserAccount) authentication.getPrincipal();
            Student student = studentRepository.findByUserAccountUsername(user.getUsername()).orElse(null);
            if (student == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student account required for synchronization.");
            if (request.getStudentId() != null && !request.getStudentId().equals(student.getId()))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sync request does not belong to the authenticated student.");
            if (request.getItems() != null && request.getItems().stream()
                    .anyMatch(item -> item.getStudentId() != null && !item.getStudentId().equals(student.getId())))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "A sync item does not belong to the authenticated student.");
            request.setStudentId(student.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Authenticated student required for synchronization.");
        }

        SyncResponse response = syncService.processSync(request);
        return ResponseEntity.ok(response);
    }
}
