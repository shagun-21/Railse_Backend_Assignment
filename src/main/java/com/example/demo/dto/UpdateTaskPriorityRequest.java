package com.example.demo.dto;

import com.example.demo.model.enums.Priority;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateTaskPriorityRequest {

    private Long taskId;
    private Priority priority;
}
