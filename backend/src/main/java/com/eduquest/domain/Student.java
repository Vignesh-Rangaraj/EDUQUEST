package com.eduquest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_account_id", nullable = false, unique = true)
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties("students")
    private Parent parent;

    @Column(name = "xp")
    private Integer xp = 0;

    @Column(name = "level")
    private Integer level = 1;

    @Column(name = "coins")
    private Integer coins = 0;

    @Column(name = "current_streak")
    private Integer currentStreak = 0;

    @Column(name = "highest_streak")
    private Integer highestStreak = 0;

    @Column(name = "last_active_date")
    private java.time.LocalDate lastActiveDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Student() {}

    public Student(Long id, UserAccount userAccount, Classroom classroom, Parent parent, Integer xp, Integer level, Integer coins, Integer currentStreak, Integer highestStreak, java.time.LocalDate lastActiveDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userAccount = userAccount;
        this.classroom = classroom;
        this.parent = parent;
        this.xp = xp != null ? xp : 0;
        this.level = level != null ? level : 1;
        this.coins = coins != null ? coins : 0;
        this.currentStreak = currentStreak != null ? currentStreak : 0;
        this.highestStreak = highestStreak != null ? highestStreak : 0;
        this.lastActiveDate = lastActiveDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.xp == null) this.xp = 0;
        if (this.level == null) this.level = 1;
        if (this.coins == null) this.coins = 0;
        if (this.currentStreak == null) this.currentStreak = 0;
        if (this.highestStreak == null) this.highestStreak = 0;
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

    public Parent getParent() { return parent; }
    public void setParent(Parent parent) { this.parent = parent; }

    public Integer getXp() { return xp != null ? xp : 0; }
    public void setXp(Integer xp) { this.xp = xp; }

    public Integer getLevel() { return level != null ? level : 1; }
    public void setLevel(Integer level) { this.level = level; }

    public Integer getCoins() { return coins != null ? coins : 0; }
    public void setCoins(Integer coins) { this.coins = coins; }

    public Integer getCurrentStreak() { return currentStreak != null ? currentStreak : 0; }
    public void setCurrentStreak(Integer currentStreak) { this.currentStreak = currentStreak; }

    public Integer getHighestStreak() { return highestStreak != null ? highestStreak : 0; }
    public void setHighestStreak(Integer highestStreak) { this.highestStreak = highestStreak; }

    public java.time.LocalDate getLastActiveDate() { return lastActiveDate; }
    public void setLastActiveDate(java.time.LocalDate lastActiveDate) { this.lastActiveDate = lastActiveDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static StudentBuilder builder() {
        return new StudentBuilder();
    }

    public static class StudentBuilder {
        private Long id;
        private UserAccount userAccount;
        private Classroom classroom;
        private Parent parent;
        private Integer xp = 0;
        private Integer level = 1;
        private Integer coins = 0;
        private Integer currentStreak = 0;
        private Integer highestStreak = 0;
        private java.time.LocalDate lastActiveDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public StudentBuilder id(Long id) { this.id = id; return this; }
        public StudentBuilder userAccount(UserAccount userAccount) { this.userAccount = userAccount; return this; }
        public StudentBuilder classroom(Classroom classroom) { this.classroom = classroom; return this; }
        public StudentBuilder parent(Parent parent) { this.parent = parent; return this; }
        public StudentBuilder xp(Integer xp) { this.xp = xp; return this; }
        public StudentBuilder level(Integer level) { this.level = level; return this; }
        public StudentBuilder coins(Integer coins) { this.coins = coins; return this; }
        public StudentBuilder currentStreak(Integer currentStreak) { this.currentStreak = currentStreak; return this; }
        public StudentBuilder highestStreak(Integer highestStreak) { this.highestStreak = highestStreak; return this; }
        public StudentBuilder lastActiveDate(java.time.LocalDate lastActiveDate) { this.lastActiveDate = lastActiveDate; return this; }
        public StudentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public StudentBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Student build() {
            return new Student(id, userAccount, classroom, parent, xp, level, coins, currentStreak, highestStreak, lastActiveDate, createdAt, updatedAt);
        }
    }
}
