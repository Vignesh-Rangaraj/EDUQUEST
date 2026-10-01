package com.eduquest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parents")
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_account_id", nullable = false, unique = true)
    private UserAccount userAccount;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("parent")
    private List<Student> students = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Parent() {}

    public Parent(Long id, UserAccount userAccount, List<Student> students, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userAccount = userAccount;
        this.students = students != null ? students : new ArrayList<>();
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

    public UserAccount getUserAccount() { return userAccount; }
    public void setUserAccount(UserAccount userAccount) { this.userAccount = userAccount; }

    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static ParentBuilder builder() {
        return new ParentBuilder();
    }

    public static class ParentBuilder {
        private Long id;
        private UserAccount userAccount;
        private List<Student> students = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ParentBuilder id(Long id) { this.id = id; return this; }
        public ParentBuilder userAccount(UserAccount userAccount) { this.userAccount = userAccount; return this; }
        public ParentBuilder students(List<Student> students) { this.students = students; return this; }
        public ParentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ParentBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Parent build() {
            return new Parent(id, userAccount, students, createdAt, updatedAt);
        }
    }
}
