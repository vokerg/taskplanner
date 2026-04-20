package com.vokerg.taskplanner.mapper;

import org.springframework.stereotype.Service;

import com.vokerg.taskplanner.dto.TaskResponse;
import com.vokerg.taskplanner.model.Task;

@Service
public class TaskMapper {
    public TaskResponse mapTaskToResponse(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getPriority(),
            task.getProjectId(),
            task.getCreatedAt(),
            task.getDueDate()
        );
    }
    
}
