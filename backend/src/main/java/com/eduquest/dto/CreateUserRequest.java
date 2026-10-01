package com.eduquest.dto;

public class CreateUserRequest {
    private String username;
    private String password;
    private String fullName;
    private Long classroomId;
    private Long parentId;

    public CreateUserRequest() {}

    public CreateUserRequest(String username, String password, String fullName, Long classroomId, Long parentId) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.classroomId = classroomId;
        this.parentId = parentId;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Long getClassroomId() { return classroomId; }
    public void setClassroomId(Long classroomId) { this.classroomId = classroomId; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public static CreateUserRequestBuilder builder() {
        return new CreateUserRequestBuilder();
    }

    public static class CreateUserRequestBuilder {
        private String username;
        private String password;
        private String fullName;
        private Long classroomId;
        private Long parentId;

        public CreateUserRequestBuilder username(String username) { this.username = username; return this; }
        public CreateUserRequestBuilder password(String password) { this.password = password; return this; }
        public CreateUserRequestBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public CreateUserRequestBuilder classroomId(Long classroomId) { this.classroomId = classroomId; return this; }
        public CreateUserRequestBuilder parentId(Long parentId) { this.parentId = parentId; return this; }

        public CreateUserRequest build() {
            return new CreateUserRequest(username, password, fullName, classroomId, parentId);
        }
    }
}
