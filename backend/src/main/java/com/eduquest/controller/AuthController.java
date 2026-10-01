package com.eduquest.controller;

import com.eduquest.domain.UserAccount;
import com.eduquest.dto.LoginRequest;
import com.eduquest.dto.LoginResponse;
import com.eduquest.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserAccount> getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserAccount)) {
            return ResponseEntity.status(401).build();
        }
        UserAccount user = (UserAccount) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }
}
