package com.vokerg.taskplanner.api;

import java.time.Instant;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vokerg.taskplanner.model.Project;

@RestController
@RequestMapping("/api/projects")
public class ProjectApi {
    @GetMapping
    public ResponseEntity<List<Project>> getProjects() {
        return ResponseEntity.ok(List.of(createStubProject("project-1")));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable String projectId) {
        return ResponseEntity.ok(createStubProject(projectId));
    }

    private Project createStubProject(String projectId) {
        Project project = new Project();
        project.setId(projectId);
        project.setTitle("Sample project");
        project.setDescription("Stub response until persistence is added");
        project.setCreatedAt(Instant.now());
        project.setCompleted(false);
        return project;
    }
}
