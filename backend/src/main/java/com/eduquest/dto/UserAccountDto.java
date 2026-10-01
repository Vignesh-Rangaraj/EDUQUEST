package com.eduquest.dto;

import com.eduquest.domain.Role;
import java.time.LocalDateTime;

public class UserAccountDto {
    private Long id;
    private String username;
    private String fullName;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserAccountDto() {}

    public UserAccountDto(Long id, String username, String fullName, Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static UserAccountDtoBuilder builder() {
        return new UserAccountDtoBuilder();
    }

    public static class UserAccountDtoBuilder {
        private Long id;
        private String username;
        private String fullName;
        private Role role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UserAccountDtoBuilder id(Long id) { this.id = id; return this; }
        public UserAccountDtoBuilder username(String username) { this.username = username; return this; }
        public UserAccountDtoBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public UserAccountDtoBuilder role(Role role) { this.role = role; return this; }
        public UserAccountDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public UserAccountDtoBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public UserAccountDto build() {
            return new UserAccountDto(id, username, fullName, role, createdAt, updatedAt);
        }
    }
}
