package com.vokerg.taskplanner.dto;

import java.util.List;

public record TaskEnvelopeResponse(
    List<TaskResponse> taskList,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
){
}