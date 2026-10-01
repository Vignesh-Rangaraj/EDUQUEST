package com.eduquest.dto;

public class AssignParentRequest {
    private Long studentId;
    private Long parentId;

    public AssignParentRequest() {}

    public AssignParentRequest(Long studentId, Long parentId) {
        this.studentId = studentId;
        this.parentId = parentId;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
}
