package com.vokerg.taskplanner.api;

import org.springframework.data.domain.Sort;

public enum TaskSortDirection {
    ASC(Sort.Direction.ASC),
    DESC(Sort.Direction.DESC);

    private final Sort.Direction direction;

    TaskSortDirection(Sort.Direction direction) {
        this.direction = direction;
    }

    public Sort.Direction direction() {
        return this.direction;
    }

    public static TaskSortDirection fromValue(String value) {
        return switch (value) {
            case "asc" -> ASC;
            case "desc" -> DESC;
            default -> throw new IllegalArgumentException("Unsupported sortDirection value: " + value);
        };
    }
}
