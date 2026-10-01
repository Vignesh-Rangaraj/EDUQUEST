package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "teachers")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_account_id", nullable = false, unique = true)
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Teacher() {}

    public Teacher(Long id, UserAccount userAccount, Classroom classroom, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userAccount = userAccount;
        this.classroom = classroom;
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

    public Classroom getClassroom() { return classroom; }
    public void setClassroom(Classroom classroom) { this.classroom = classroom; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static TeacherBuilder builder() {
        return new TeacherBuilder();
    }

    public static class TeacherBuilder {
        private Long id;
        private UserAccount userAccount;
        private Classroom classroom;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public TeacherBuilder id(Long id) { this.id = id; return this; }
        public TeacherBuilder userAccount(UserAccount userAccount) { this.userAccount = userAccount; return this; }
        public TeacherBuilder classroom(Classroom classroom) { this.classroom = classroom; return this; }
        public TeacherBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public TeacherBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Teacher build() {
            return new Teacher(id, userAccount, classroom, createdAt, updatedAt);
        }
    }
}
