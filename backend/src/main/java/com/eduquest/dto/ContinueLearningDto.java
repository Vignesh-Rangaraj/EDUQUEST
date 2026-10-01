package com.eduquest.dto;

public class ContinueLearningDto {

    private Long moduleId;
    private String moduleName;
    private Long lessonId;
    private String lessonTitle;
    private Double progressPercentage;
    private Long nextLessonId;

    public ContinueLearningDto() {}

    public ContinueLearningDto(Long moduleId, String moduleName, Long lessonId, String lessonTitle, Double progressPercentage, Long nextLessonId) {
        this.moduleId = moduleId;
        this.moduleName = moduleName;
        this.lessonId = lessonId;
        this.lessonTitle = lessonTitle;
        this.progressPercentage = progressPercentage;
        this.nextLessonId = nextLessonId;
    }

    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }

    public String getLessonTitle() { return lessonTitle; }
    public void setLessonTitle(String lessonTitle) { this.lessonTitle = lessonTitle; }

    public Double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Double progressPercentage) { this.progressPercentage = progressPercentage; }

    public Long getNextLessonId() { return nextLessonId; }
    public void setNextLessonId(Long nextLessonId) { this.nextLessonId = nextLessonId; }
}
