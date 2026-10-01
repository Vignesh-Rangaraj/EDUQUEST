package com.eduquest.dto;

import java.util.ArrayList;
import java.util.List;

public class SyncRequestDto {

    private Long studentId;
    private List<SyncItemDto> items = new ArrayList<>();

    public SyncRequestDto() {}

    public SyncRequestDto(Long studentId, List<SyncItemDto> items) {
        this.studentId = studentId;
        this.items = items;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public List<SyncItemDto> getItems() { return items; }
    public void setItems(List<SyncItemDto> items) { this.items = items; }
}
