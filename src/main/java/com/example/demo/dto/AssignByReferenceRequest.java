package com.example.demo.dto;

import com.example.demo.model.enums.ReferenceType;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AssignByReferenceRequest {
    private Long referenceId;
    private ReferenceType referenceType;
    private Long assigneeId;
}

