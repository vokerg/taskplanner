package com.vokerg.taskplanner.api;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vokerg.taskplanner.dto.CreateProjectRequest;
import com.vokerg.taskplanner.dto.ProjectResponse;
import com.vokerg.taskplanner.dto.UpdateProjectRequest;
import com.vokerg.taskplanner.service.ProjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
public class ProjectApi {

    private final ProjectService projectService;

    public ProjectApi(ProjectService projectService) {
        this.projectService = projectService;
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

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable String projectId) {
        this.projectService.removeProject(projectId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> replaceProject(@PathVariable String projectId, @RequestBody UpdateProjectRequest project) {
        return this.projectService.replaceProject(projectId, project)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable String projectId, @Valid @RequestBody UpdateProjectRequest request) {
        return this.projectService.updateProject(projectId, request)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
