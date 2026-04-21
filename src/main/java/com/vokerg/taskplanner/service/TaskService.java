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
import com.vokerg.taskplanner.repository.TaskRepository;

@Service
public class TaskService {

    private final TaskMapper taskMapper;
    private final ProjectService projectService;
    private final TaskRepository taskRepository;

    public TaskService(TaskMapper taskMapper, ProjectService projectService, TaskRepository taskRepository) {
        this.taskMapper = taskMapper;
        this.projectService = projectService;
        this.taskRepository = taskRepository;
    }

    public List<TaskResponse> getTasksForProject(String projectId) {
        return this.taskRepository.getTasksByProjectId(projectId).stream()
            .map(this.taskMapper::mapTaskToResponse)
            .toList();
    }

    public Optional<TaskResponse> getTaskById(String taskId) {
        return Optional.ofNullable(this.taskRepository.getTaskById(taskId))
            .map(this.taskMapper::mapTaskToResponse);
    }

    public TaskResponse createTask(String projectId, CreateTaskRequest request) {
        ProjectResponse projectResponse = this.projectService.getProjectById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
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

        this.taskRepository.saveTask(createdTask);

        return this.taskMapper.mapTaskToResponse(createdTask);
    }

    public Optional<TaskResponse> changeTaskStatus(String taskId, ChangeTaskStatusRequest request) {
        Task existingTask = this.taskRepository.getTaskById(taskId);
        if (existingTask == null) {
            return Optional.empty();
        }
        if (!allowedStatusTransition(existingTask.getStatus(), request.status())) {

            throw new BusinessRuleViolationException("Cannot move task back to IN_PROGRESS from DONE");
        }
        existingTask.setStatus(request.status());
        this.taskRepository.saveTask(existingTask);
        return Optional.of(this.taskMapper.mapTaskToResponse(existingTask));
    }

    private boolean allowedStatusTransition(TaskStatus status, TaskStatus status2) {
        if ((status == TaskStatus.DONE && status2 == TaskStatus.IN_PROGRESS)
            || (status == TaskStatus.DONE && status2 == TaskStatus.TODO))
             {
            return false;
        }
        return true;
    }

    public void removeTask(String taskId) {
        this.taskRepository.deleteTask(taskId);
    }

    public Optional<TaskResponse> replaceTask(String taskId, UpdateTaskRequest task) {
        Task existingTask = this.taskRepository.getTaskById(taskId);
        if (existingTask == null) {
            return Optional.empty();
        }
        existingTask.setTitle(task.title());
        existingTask.setDescription(task.description());
        existingTask.setPriority(task.priority());
        existingTask.setDueDate(task.dueDate());
        this.taskRepository.saveTask(existingTask);

        return Optional.of(this.taskMapper.mapTaskToResponse(existingTask));
    }
}
