package com.vokerg.taskplanner.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vokerg.taskplanner.dto.ChangeTaskStatusRequest;
import com.vokerg.taskplanner.dto.CreateTaskRequest;
import com.vokerg.taskplanner.dto.ProjectResponse;
import com.vokerg.taskplanner.dto.TaskResponse;
import com.vokerg.taskplanner.dto.UpdateTaskRequest;
import com.vokerg.taskplanner.exception.BusinessRuleViolationException;
import com.vokerg.taskplanner.exception.ProjectNotFoundException;
import com.vokerg.taskplanner.exception.TaskNotFoundException;
import com.vokerg.taskplanner.mapper.TaskMapper;
import com.vokerg.taskplanner.model.Project;
import com.vokerg.taskplanner.model.Task;
import com.vokerg.taskplanner.model.TaskPriority;
import com.vokerg.taskplanner.model.TaskStatus;
import com.vokerg.taskplanner.repository.ProjectRepository;
import com.vokerg.taskplanner.repository.TaskRepository;

@Service
public class TaskService {

    private final TaskMapper taskMapper;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public TaskService(
        TaskMapper taskMapper,
        ProjectService projectService,
        ProjectRepository projectRepository,
        TaskRepository taskRepository
    ) {
        this.taskMapper = taskMapper;
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    public List<TaskResponse> getTasksForProject(String projectId) {
        return this.taskRepository.findByProjectId(projectId).stream()
            .map(this.taskMapper::mapTaskToResponse)
            .toList();
    }

    public TaskResponse getTaskById(String taskId) {
        Task task = this.taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
        return this.taskMapper.mapTaskToResponse(task);
    }

    public TaskResponse createTask(String projectId, CreateTaskRequest request) {
        ProjectResponse projectResponse = this.projectService.getProjectById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        if (projectResponse.completed()) {
            throw new BusinessRuleViolationException("Cannot add task to a completed project");
        }
        Project project = this.projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));

        Task createdTask = new Task();
        Instant now = Instant.now();

        createdTask.setTitle(request.title());
        createdTask.setDescription(request.description());
        createdTask.setCreatedAt(now);
        createdTask.setStatus(TaskStatus.TODO);
        createdTask.setPriority(request.priority() != null ? request.priority() : TaskPriority.MEDIUM);
        createdTask.setProject(project);
        createdTask.setDueDate(request.dueDate());

        this.taskRepository.save(createdTask);

        return this.taskMapper.mapTaskToResponse(createdTask);
    }

    public TaskResponse changeTaskStatus(String taskId, ChangeTaskStatusRequest request) {
        Task existingTask = this.taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
        if (!allowedStatusTransition(existingTask.getStatus(), request.status())) {
            throw new BusinessRuleViolationException("Cannot move task back to IN_PROGRESS from DONE");
        }
        existingTask.setStatus(request.status());
        this.taskRepository.save(existingTask);
        return this.taskMapper.mapTaskToResponse(existingTask);
    }

    private boolean allowedStatusTransition(TaskStatus status, TaskStatus nextStatus) {
        return !((status == TaskStatus.DONE && nextStatus == TaskStatus.IN_PROGRESS)
            || (status == TaskStatus.DONE && nextStatus == TaskStatus.TODO));
    }

    public void removeTask(String taskId) {
        this.taskRepository.deleteById(taskId);
    }

    public TaskResponse replaceTask(String taskId, UpdateTaskRequest task) {
        Task existingTask = this.taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
        existingTask.setTitle(task.title());
        existingTask.setDescription(task.description());
        existingTask.setPriority(task.priority());
        existingTask.setDueDate(task.dueDate());
        this.taskRepository.save(existingTask);

        return this.taskMapper.mapTaskToResponse(existingTask);
    }
}
