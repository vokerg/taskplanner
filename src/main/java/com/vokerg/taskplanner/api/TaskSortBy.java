package com.vokerg.taskplanner.api;

public enum TaskSortBy {
    NAME("title"),
    DUE_DATE("dueDate"),
    STATUS("status"),
    PRIORITY("priority");

    private final String property;

    TaskSortBy(String property) {
        this.property = property;
    }

    public String property() {
        return this.property;
    }

    public static TaskSortBy fromValue(String value) {
        return switch (value) {
            case "name" -> NAME;
            case "dueDate" -> DUE_DATE;
            case "status" -> STATUS;
            case "priority" -> PRIORITY;
            default -> throw new IllegalArgumentException("Unsupported sortBy value: " + value);
        };
    }
}
