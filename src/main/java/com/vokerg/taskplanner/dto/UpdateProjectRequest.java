package com.vokerg.taskplanner.dto;

import jakarta.validation.constraints.Size;

public record UpdateProjectRequest(
    @Size(min = 3, max = 255)
    String title,

    @Size(min = 3, max = 1000)
    String description,

    Boolean completed
) {
}
