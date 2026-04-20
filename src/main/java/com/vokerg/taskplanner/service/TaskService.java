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
        Instant now = Instant.now();

        if (createdTask.getId() == null || createdTask.getId().isBlank()) {
            createdTask.setId("task-" + now.toEpochMilli());
        }

        if (createdTask.getCreatedAt() == null) {
            createdTask.setCreatedAt(now);
        }

        if (createdTask.getStatus() == null) {
            createdTask.setStatus(TaskStatus.TODO);
        }

        if (createdTask.getPriority() == null) {
            createdTask.setPriority(TaskPriority.MEDIUM);
        }

        return createdTask;
    }

    public Optional<Task> patchTask(String taskId, Task task) {
        Task existingTask = createStubTask(taskId, task.getProjectId());

        if (task.getTitle() != null) {
            existingTask.setTitle(task.getTitle());
        }

        if (task.getDescription() != null) {
            existingTask.setDescription(task.getDescription());
        }

        if (task.getStatus() != null) {
            existingTask.setStatus(task.getStatus());
        }

        if (task.getPriority() != null) {
            existingTask.setPriority(task.getPriority());
        }

        if (task.getProjectId() != null) {
            existingTask.setProjectId(task.getProjectId());
        }

        if (task.getCreatedAt() != null) {
            existingTask.setCreatedAt(task.getCreatedAt());
        }

        if (task.getDueDate() != null) {
            existingTask.setDueDate(task.getDueDate());
        }

        return Optional.of(existingTask);
    }

    public void removeTask(String taskId) {
    }
}
