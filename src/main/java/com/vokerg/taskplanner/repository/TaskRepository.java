package com.vokerg.taskplanner.repository;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.vokerg.taskplanner.model.Task;
import com.vokerg.taskplanner.model.TaskPriority;
import com.vokerg.taskplanner.model.TaskStatus;

@Service
public class TaskRepository {

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

    public Task getTasksByProjectId(String projectId) {
        return this.createStubTask("task-1", projectId);
    }

    public Task getTaskById(String taskId) {
        return this.createStubTask(taskId, null);
    }
    
}
