package com.vokerg.taskplanner.dto;

import java.time.Instant;

public record ProjectResponse(
    String id,
    String title,
    String description,
    Instant createdAt,
    boolean completed
) {
}
