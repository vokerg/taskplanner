package com.vokerg.taskplanner.repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.vokerg.taskplanner.model.Project;

@Service
public class ProjectRepository {

    private final Map<String, Project> projects = new LinkedHashMap<>();

    public ProjectRepository() {
        Project sampleProject = this.createStubProject("project-1");
        this.projects.put(sampleProject.getId(), sampleProject);
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

    public Project getProjectById(String projectId) {
        return this.projects.get(projectId);
    }

    public List<Project> getAllProjects() {
        return new ArrayList<>(this.projects.values());
    }

    public void saveProject(Project project) {
        this.projects.put(project.getId(), project);
    }

    public void deleteProject(String projectId) {
        this.projects.remove(projectId);
    }
}
