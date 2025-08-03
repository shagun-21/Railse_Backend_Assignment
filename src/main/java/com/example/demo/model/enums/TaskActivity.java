package com.example.demo.model.enums;

import lombok.Data;

import java.time.Instant;

@Data
public class TaskActivity {
    private String message;
    private Instant timestamp;

    public TaskActivity(String message,Instant timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }
}
