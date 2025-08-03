package com.example.demo.model.enums;


import lombok.Data;

import java.time.Instant;

@Data
public class TaskComment {
    private String commentText;
    private Instant commentedAt;

    public TaskComment(String commentText, Instant commentedAt) {

        this.commentText = commentText;
        this.commentedAt = commentedAt;
    }
}
