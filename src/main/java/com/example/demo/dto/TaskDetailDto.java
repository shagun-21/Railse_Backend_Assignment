package com.example.demo.dto;

import com.example.demo.model.enums.TaskActivity;
import com.example.demo.model.enums.TaskComment;
import com.example.demo.model.enums.TaskManagement;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskDetailDto {
    private TaskManagement task;
    private List<TaskComment> comments;
    private List<TaskActivity> activityHistory;
}
