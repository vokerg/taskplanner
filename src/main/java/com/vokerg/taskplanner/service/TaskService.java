package com.vokerg.taskplanner.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.vokerg.taskplanner.dto.ChangeTaskStatusRequest;
import com.vokerg.taskplanner.dto.CreateTaskRequest;
import com.vokerg.taskplanner.dto.ProjectResponse;
import com.vokerg.taskplanner.dto.TaskResponse;
import com.vokerg.taskplanner.dto.UpdateTaskRequest;
import com.vokerg.taskplanner.mapper.TaskMapper;
import com.vokerg.taskplanner.model.Task;
import com.vokerg.taskplanner.model.TaskPriority;
import com.vokerg.taskplanner.model.TaskStatus;

@Service
public class TaskService {

    private final TaskMapper taskMapper;
    private final ProjectService projectService;

    public TaskService(TaskMapper taskMapper, ProjectService projectService) {
        this.taskMapper = taskMapper;
        this.projectService = projectService;
    }

    public List<TaskResponse> getTasksForProject(String projectId) {
        // Implementation for fetching tasks for a specific project
        return List.of(this.taskMapper.mapTaskToResponse(this.createStubTask("task-1", projectId)));
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

    public Optional<TaskResponse> getTaskById(String taskId) {
        return Optional.of(createStubTask(taskId, null)).map(task -> this.taskMapper.mapTaskToResponse(task));
    }

    public TaskResponse createTask(String projectId, CreateTaskRequest request) {
        ProjectResponse projectResponse = this.projectService.getProjectById(projectId).orElseThrow(() -> new IllegalArgumentException("Project not found"));
        if (projectResponse.completed()) {
            throw new BusinessRuleViolationException("Cannot add task to a completed project");
        }

        Task createdTask = new Task();
        Instant now = Instant.now();

        createdTask.setId("task-" + now.toEpochMilli());
        createdTask.setTitle(request.title());
        createdTask.setDescription(request.description());
        createdTask.setCreatedAt(now);
        createdTask.setStatus(TaskStatus.TODO);
        createdTask.setPriority(request.priority() != null ? request.priority() : TaskPriority.MEDIUM);
        createdTask.setProjectId(projectId);
        createdTask.setDueDate(request.dueDate());

        return this.taskMapper.mapTaskToResponse(createdTask);
    }

    public Optional<TaskResponse> changeTaskStatus(String taskId, ChangeTaskStatusRequest request) {
        Task existingTask = createStubTask(taskId, null);
        if (!allowedStatusTransition(existingTask.getStatus(), request.status())) {

            throw new BusinessRuleViolationException("Cannot move task back to IN_PROGRESS from DONE");
        }
        existingTask.setStatus(request.status());
        return Optional.of(this.taskMapper.mapTaskToResponse(existingTask));
    }

    private boolean allowedStatusTransition(TaskStatus status, TaskStatus status2) {
        if (status == TaskStatus.DONE && status2 == TaskStatus.IN_PROGRESS) {
            return false;
        }
        return true;
    }

    public void removeTask(String taskId) {
    }

    public Optional<TaskResponse> replaceTask(String taskId, UpdateTaskRequest task) {
        Task existingTask = createStubTask(taskId, null);
            existingTask.setTitle(task.title());
            existingTask.setDescription(task.description());
            existingTask.setPriority(task.priority());
            existingTask.setDueDate(task.dueDate());
            
            return Optional.of(this.taskMapper.mapTaskToResponse(existingTask));
    }
}
