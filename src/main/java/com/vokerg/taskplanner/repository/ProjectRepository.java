package com.vokerg.taskplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vokerg.taskplanner.model.Project;

public interface ProjectRepository extends JpaRepository<Project, String> {
}
