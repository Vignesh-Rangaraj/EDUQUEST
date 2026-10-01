package com.eduquest.dto;

import com.eduquest.domain.Role;

public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String fullName;
    private Role role;

    public LoginResponse() {}

    public LoginResponse(String token, Long userId, String username, String fullName, Role role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public static LoginResponseBuilder builder() {
        return new LoginResponseBuilder();
    }

    public static class LoginResponseBuilder {
        private String token;
        private Long userId;
        private String username;
        private String fullName;
        private Role role;

        public LoginResponseBuilder token(String token) { this.token = token; return this; }
        public LoginResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public LoginResponseBuilder username(String username) { this.username = username; return this; }
        public LoginResponseBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public LoginResponseBuilder role(Role role) { this.role = role; return this; }

        public LoginResponse build() {
            return new LoginResponse(token, userId, username, fullName, role);
        }
    }
}
