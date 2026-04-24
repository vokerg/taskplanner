package com.vokerg.taskplanner.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;

import com.vokerg.taskplanner.api.TaskSortBy;
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

    public List<TaskResponse> getTasksForProject(String projectId, TaskStatus status, TaskPriority priority) {
        return this.getTasksForProject(projectId, status, priority, null, null, null);
    }

    public List<TaskResponse> getTasksForProject(
        String projectId,
        TaskStatus status,
        TaskPriority priority,
        Instant dueDateAfter,
        Instant dueDateBefore,
        TaskSortBy sortBy
    ) {
        Specification<Task> specification = (root, query, criteriaBuilder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("projectId"), projectId));

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (priority != null) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), priority));
            }
            if (dueDateAfter != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate"), dueDateAfter));
            }
            if (dueDateBefore != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dueDate"), dueDateBefore));
            }

            return criteriaBuilder.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };

        Sort sort = sortBy == null ? Sort.unsorted() : Sort.by(Sort.Direction.ASC, sortBy.property());
        List<Task> tasks = this.taskRepository.findAll(specification, sort);

        return tasks.stream()
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
