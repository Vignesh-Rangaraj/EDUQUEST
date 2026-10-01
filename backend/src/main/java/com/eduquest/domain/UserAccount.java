package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_accounts")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UserAccount() {}

    public UserAccount(Long id, String username, String password, String fullName, Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static UserAccountBuilder builder() {
        return new UserAccountBuilder();
    }

    public static class UserAccountBuilder {
        private Long id;
        private String username;
        private String password;
        private String fullName;
        private Role role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UserAccountBuilder id(Long id) { this.id = id; return this; }
        public UserAccountBuilder username(String username) { this.username = username; return this; }
        public UserAccountBuilder password(String password) { this.password = password; return this; }
        public UserAccountBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public UserAccountBuilder role(Role role) { this.role = role; return this; }
        public UserAccountBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public UserAccountBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public UserAccount build() {
            return new UserAccount(id, username, password, fullName, role, createdAt, updatedAt);
        }
    }
}
