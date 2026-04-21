package com.vokerg.taskplanner.dto;

import com.vokerg.taskplanner.model.TaskStatus;

import jakarta.validation.constraints.NotNull;

public record ChangeTaskStatusRequest(
    @NotNull
    TaskStatus status
) {
}
