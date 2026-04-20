package com.vokerg.taskplanner.dto;

import java.time.Instant;

import com.vokerg.taskplanner.model.TaskPriority;
import com.vokerg.taskplanner.model.TaskStatus;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(
    @Size(min = 3, max = 255)
    String title,

    @Size(min = 3, max = 1000)
    String description,

    TaskStatus status,

    TaskPriority priority,

    String projectId,

    @FutureOrPresent
    Instant dueDate
) {
}
