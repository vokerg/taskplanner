package com.vokerg.taskplanner.mapper;

import org.springframework.stereotype.Service;

import com.vokerg.taskplanner.dto.ProjectResponse;
import com.vokerg.taskplanner.model.Project;

@Service
public class ProjectMapper {
    public ProjectResponse mapProjectToResponse(Project project) {
        return new ProjectResponse(
            project.getId(),
            project.getTitle(),
            project.getDescription(),
            project.getCreatedAt(),
            project.isCompleted()
        );
    }
}
