package com.vokerg.taskplanner.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vokerg.taskplanner.model.Project;

@Service
public class ProjectRepository {

    private Project createStubProject(String projectId) {
        Project project = new Project();
        project.setId(projectId);
        project.setTitle("Sample project");
        project.setDescription("Stub response until persistence is added");
        project.setCreatedAt(Instant.now());
        project.setCompleted(false);
        return project;
    }

    public Project getProjectById(String projectId) {
        return this.createStubProject(projectId);
    }

    public List<Project> getAllProjects() {
        return List.of(this.createStubProject("project-1"));
    }

    
}
