package com.vokerg.taskplanner.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import com.vokerg.taskplanner.model.Task;
import com.vokerg.taskplanner.model.TaskPriority;
import com.vokerg.taskplanner.model.TaskStatus;

@Service
public class TaskService {
    public List<Task> getTasksForProject(String projectId) {
        // Implementation for fetching tasks for a specific project
        return List.of(createStubTask("task-1", projectId));
    }

    private Task createStubTask(String taskId, String projectId) {
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Sample task");
        task.setDescription("Stub response until persistence is added");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.MEDIUM);
        task.setProjectId(projectId);
        task.setCreatedAt(Instant.now());
        return task;
    }

    public Optional<Task> getTaskById(String taskId) {
        return Optional.of(createStubTask(taskId, null));
    }

    public @Nullable Task createTask(Task task) {
        Task createdTask = task;

        if (createdTask.getId() == null || createdTask.getId().isBlank()) {
            createdTask.setId("task-" + Instant.now().toEpochMilli());
        }

        if (createdTask.getCreatedAt() == null) {
            createdTask.setCreatedAt(Instant.now());
        }
        return createdTask;
    }
}
