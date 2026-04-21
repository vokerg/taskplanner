package com.vokerg.taskplanner.repository;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.vokerg.taskplanner.model.Task;
import com.vokerg.taskplanner.model.TaskPriority;
import com.vokerg.taskplanner.model.TaskStatus;

@Service
public class TaskRepository {

    private final Map<String, Task> tasks = new LinkedHashMap<>();

    public TaskRepository() {
        Task sampleTask = this.createStubTask("task-1", "project-1");
        this.tasks.put(sampleTask.getId(), sampleTask);
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

    public List<Task> getTasksByProjectId(String projectId) {
        return this.tasks.values().stream()
            .filter(task -> projectId.equals(task.getProjectId()))
            .toList();
    }

    public Task getTaskById(String taskId) {
        return this.tasks.get(taskId);
    }

    public void saveTask(Task task) {
        this.tasks.put(task.getId(), task);
    }

    public void deleteTask(String taskId) {
        this.tasks.remove(taskId);
    }
}
