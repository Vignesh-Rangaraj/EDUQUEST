package com.eduquest.dto;

public class UpdateUserRequest {
    private String fullName;
    private Long classroomId;
    private Long parentId;

    public UpdateUserRequest() {}

    public UpdateUserRequest(String fullName, Long classroomId, Long parentId) {
        this.fullName = fullName;
        this.classroomId = classroomId;
        this.parentId = parentId;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Long getClassroomId() { return classroomId; }
    public void setClassroomId(Long classroomId) { this.classroomId = classroomId; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
}
