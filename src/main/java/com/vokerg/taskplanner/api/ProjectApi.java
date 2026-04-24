package com.vokerg.taskplanner.api;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vokerg.taskplanner.dto.CreateProjectRequest;
import com.vokerg.taskplanner.dto.CreateTaskRequest;
import com.vokerg.taskplanner.dto.ProjectResponse;
import com.vokerg.taskplanner.dto.TaskResponse;
import com.vokerg.taskplanner.dto.UpdateProjectRequest;
import com.vokerg.taskplanner.model.TaskPriority;
import com.vokerg.taskplanner.model.TaskStatus;
import com.vokerg.taskplanner.service.ProjectService;
import com.vokerg.taskplanner.service.TaskService;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
public class ProjectApi {

    private final ProjectService projectService;
    private final TaskService taskService;

    public ProjectApi(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjects() {
        return ResponseEntity.ok(this.projectService.getAllProjects());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable String projectId) {
        return ResponseEntity.of(this.projectService.getProjectById(projectId));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request) {
        ProjectResponse createdProject = this.projectService.createProject(request);
        return ResponseEntity
            .created(URI.create("/api/projects/" + createdProject.id()))
            .body(createdProject);
    }

    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<TaskResponse> createTask(@PathVariable String projectId, @Valid @RequestBody CreateTaskRequest request) {
        TaskResponse createdTask = this.taskService.createTask(projectId, request);
        return ResponseEntity
            .created(URI.create("/api/tasks/" + createdTask.id()))
            .body(createdTask);
    }

    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasksByProjectId(
        @Parameter(description = "Project ID to fetch tasks for", example = "550e8400-e29b-41d4-a716-446655440000")
        @PathVariable String projectId,
        @Parameter(description = "Optional task status filter", example = "IN_PROGRESS")
        @RequestParam(required = false) TaskStatus status,
        @Parameter(description = "Optional task priority filter", example = "HIGH")
        @RequestParam(required = false) TaskPriority priority,
        @Parameter(description = "Optional filter for tasks due on or before this instant", example = "2026-05-01T00:00:00Z")
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dueDateBefore,
        @Parameter(description = "Optional filter for tasks due on or after this instant", example = "2026-04-25T00:00:00Z")
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dueDateAfter,
        @Parameter(description = "Optional ascending sort field: name, dueDate, status, or priority", example = "dueDate")
        @RequestParam(required = false) TaskSortBy sortBy
    ) {
        return ResponseEntity.ok(this.taskService.getTasksForProject(projectId, status, priority, dueDateAfter, dueDateBefore, sortBy));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable String projectId) {
        this.projectService.removeProject(projectId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> replaceProject(@PathVariable String projectId, @Valid @RequestBody UpdateProjectRequest project) {
        return ResponseEntity.of(this.projectService.replaceProject(projectId, project));
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable String projectId, @Valid @RequestBody UpdateProjectRequest request) {
        return ResponseEntity.of(this.projectService.updateProject(projectId, request));
    }
}
