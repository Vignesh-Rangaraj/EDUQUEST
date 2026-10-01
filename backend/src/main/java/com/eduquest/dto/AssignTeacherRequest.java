package com.eduquest.dto;

public class AssignTeacherRequest {
    private Long teacherId;
    private Long classroomId;

    public AssignTeacherRequest() {}

    public AssignTeacherRequest(Long teacherId, Long classroomId) {
        this.teacherId = teacherId;
        this.classroomId = classroomId;
    }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public Long getClassroomId() { return classroomId; }
    public void setClassroomId(Long classroomId) { this.classroomId = classroomId; }
}
