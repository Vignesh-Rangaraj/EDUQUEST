package com.eduquest.dto;

import java.util.ArrayList;
import java.util.List;

public class SyncResponse {

    private boolean success;
    private List<String> syncedItems = new ArrayList<>();
    private List<String> failedItems = new ArrayList<>();
    private List<String> errors = new ArrayList<>();

    public SyncResponse() {}

    public SyncResponse(boolean success, List<String> syncedItems, List<String> failedItems, List<String> errors) {
        this.success = success;
        this.syncedItems = syncedItems;
        this.failedItems = failedItems;
        this.errors = errors;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public List<String> getSyncedItems() { return syncedItems; }
    public void setSyncedItems(List<String> syncedItems) { this.syncedItems = syncedItems; }

    public List<String> getFailedItems() { return failedItems; }
    public void setFailedItems(List<String> failedItems) { this.failedItems = failedItems; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}
