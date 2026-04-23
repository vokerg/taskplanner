package com.vokerg.taskplanner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vokerg.taskplanner.model.Task;

public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByProjectId(String projectId);
}
