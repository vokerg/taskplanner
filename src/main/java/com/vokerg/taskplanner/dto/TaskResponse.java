package com.vokerg.taskplanner.dto;

import java.time.Instant;

import com.vokerg.taskplanner.model.TaskPriority;
import com.vokerg.taskplanner.model.TaskStatus;

public record TaskResponse(
    String id,
    String title,
    String description,
    TaskStatus status,
    TaskPriority priority,
    String projectId,
    Instant createdAt,
    Instant dueDate
) {
}
