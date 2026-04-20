package com.vokerg.taskplanner.dto;

import java.time.Instant;

import com.vokerg.taskplanner.model.TaskPriority;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
    @NotBlank
    @Size(min = 3, max = 255)
    String title,

    @NotBlank
    @Size(min = 3, max = 1000)
    String description,

    @NotBlank
    TaskPriority priority,

    @FutureOrPresent
    Instant dueDate
) {
}