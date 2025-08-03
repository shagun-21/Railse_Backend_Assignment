package com.example.demo.model.enums;

import lombok.Data;

import java.util.List;

@Data
public class TaskManagement {
    private Long id;
    private Long referenceId;
    private ReferenceType referenceType;
    private Task task;
    private String description;
    private TaskStatus status;
    private Long assigneeId;
    private Long taskDeadlineTime;
    private Priority priority;
    private List<TaskComment> comments;
    private List<TaskActivity> activityHistory;
}

