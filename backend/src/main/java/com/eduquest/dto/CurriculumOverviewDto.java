package com.eduquest.dto;

import java.util.List;

public class CurriculumOverviewDto {

    private long totalSubjects;
    private long totalModules;
    private long totalActivities;
    private long publishedModules;
    private long publishedActivities;
    private List<ModuleDto> modules;

    public CurriculumOverviewDto() {}

    public CurriculumOverviewDto(long totalSubjects, long totalModules, long totalActivities, long publishedModules, long publishedActivities, List<ModuleDto> modules) {
        this.totalSubjects = totalSubjects;
        this.totalModules = totalModules;
        this.totalActivities = totalActivities;
        this.publishedModules = publishedModules;
        this.publishedActivities = publishedActivities;
        this.modules = modules;
    }

    public long getTotalSubjects() { return totalSubjects; }
    public void setTotalSubjects(long totalSubjects) { this.totalSubjects = totalSubjects; }

    public long getTotalModules() { return totalModules; }
    public void setTotalModules(long totalModules) { this.totalModules = totalModules; }

    public long getTotalActivities() { return totalActivities; }
    public void setTotalActivities(long totalActivities) { this.totalActivities = totalActivities; }

    public long getPublishedModules() { return publishedModules; }
    public void setPublishedModules(long publishedModules) { this.publishedModules = publishedModules; }

    public long getPublishedActivities() { return publishedActivities; }
    public void setPublishedActivities(long publishedActivities) { this.publishedActivities = publishedActivities; }

    public List<ModuleDto> getModules() { return modules; }
    public void setModules(List<ModuleDto> modules) { this.modules = modules; }
}
