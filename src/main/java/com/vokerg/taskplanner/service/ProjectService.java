package com.vokerg.taskplanner.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.vokerg.taskplanner.dto.CreateProjectRequest;
import com.vokerg.taskplanner.dto.ProjectResponse;
import com.vokerg.taskplanner.dto.UpdateProjectRequest;
import com.vokerg.taskplanner.mapper.ProjectMapper;
import com.vokerg.taskplanner.model.Project;
import com.vokerg.taskplanner.repository.ProjectRepository;

@Service
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectMapper projectMapper, ProjectRepository projectRepository) {
        this.projectMapper = projectMapper;
        this.projectRepository = projectRepository;
    }

    public List<ProjectResponse> getAllProjects() {
        return this.projectRepository.findAll().stream()
            .map(this.projectMapper::mapProjectToResponse)
            .toList();
    }

    public Optional<ProjectResponse> getProjectById(String projectId) {
        return this.projectRepository.findById(projectId)
            .map(this.projectMapper::mapProjectToResponse);
    }

    public ProjectResponse createProject(CreateProjectRequest request) {
        Project createdProject = new Project();
        Instant now = Instant.now();

        createdProject.setTitle(request.title());
        createdProject.setDescription(request.description());
        createdProject.setCreatedAt(now);
        createdProject.setCompleted(false);

        this.projectRepository.save(createdProject);

        return this.projectMapper.mapProjectToResponse(createdProject);
    }

    public Optional<ProjectResponse> updateProject(String projectId, UpdateProjectRequest request) {
        Optional<Project> existingProjectOptional = this.projectRepository.findById(projectId);
        if (existingProjectOptional.isEmpty()) {
            return Optional.empty();
        }
        Project existingProject = existingProjectOptional.get();

        if (request.title() != null) {
            existingProject.setTitle(request.title());
        }
        if (request.description() != null) {
            existingProject.setDescription(request.description());
        }
        if (request.completed() != null) {
            existingProject.setCompleted(request.completed());
        }

        this.projectRepository.save(existingProject);

        return Optional.of(this.projectMapper.mapProjectToResponse(existingProject));
    }

    public Optional<ProjectResponse> replaceProject(String projectId, UpdateProjectRequest request) {
        Optional<Project> existingProjectOptional = this.projectRepository.findById(projectId);
        if (existingProjectOptional.isEmpty()) {
            return Optional.empty();
        }
        Project existingProject = existingProjectOptional.get();

        existingProject.setTitle(request.title());
        existingProject.setDescription(request.description());
        existingProject.setCompleted(request.completed() != null ? request.completed() : false);

        this.projectRepository.save(existingProject);

        return Optional.of(this.projectMapper.mapProjectToResponse(existingProject));
    }

    public void removeProject(String projectId) {
        this.projectRepository.deleteById(projectId);
    }
}
