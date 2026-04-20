package com.vokerg.taskplanner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
    @NotBlank
    @Size(min = 3, max = 255)
    String title,

    @NotBlank
    @Size(min = 3, max = 1000)
    String description
) {
}
