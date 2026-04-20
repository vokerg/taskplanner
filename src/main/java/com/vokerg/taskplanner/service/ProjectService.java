package com.vokerg.taskplanner.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vokerg.taskplanner.dto.CreateProjectRequest;
import com.vokerg.taskplanner.dto.ProjectResponse;
import com.vokerg.taskplanner.dto.UpdateProjectRequest;
import com.vokerg.taskplanner.mapper.ProjectMapper;
import com.vokerg.taskplanner.model.Project;

@Service
public class ProjectService {

    @Autowired
    ProjectMapper projectMapper;

    public List<ProjectResponse> getAllProjects() {
        // Implementation for fetching all projects
        return List.of(this.projectMapper.mapProjectToResponse(this.createStubProject("project-1")));
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

    public Optional<ProjectResponse> getProjectById(String projectId) {
        return Optional.of(createStubProject(projectId)).map(project -> this.projectMapper.mapProjectToResponse(project));
    }

    public ProjectResponse createProject(CreateProjectRequest request) {
        Project createdProject = new Project();
        Instant now = Instant.now();

        createdProject.setId("project-" + now.toEpochMilli());
        createdProject.setTitle(request.title());
        createdProject.setDescription(request.description());
        createdProject.setCreatedAt(now);
        createdProject.setCompleted(false);

        return this.projectMapper.mapProjectToResponse(createdProject);
    }

    public Optional<ProjectResponse> updateProject(String projectId, UpdateProjectRequest request) {
        Project existingProject = createStubProject(projectId);
        
        if (request.title() != null) {
            existingProject.setTitle(request.title());
        }
        if (request.description() != null) {
            existingProject.setDescription(request.description());
        }
        if (request.completed() != null) {
            existingProject.setCompleted(request.completed());
        }
        
        return Optional.of(this.projectMapper.mapProjectToResponse(existingProject));
    }

    public Optional<ProjectResponse> replaceProject(String projectId, UpdateProjectRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'replaceProject'");
    }

    public void removeProject(String projectId) {
    }
}
