package com.vokerg.taskplanner.model;

import java.time.Instant;

import lombok.Data;

@Data
public class Task {
    private String id;

    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private String projectId;

    private Instant createdAt;

    private Instant dueDate;
    
}
