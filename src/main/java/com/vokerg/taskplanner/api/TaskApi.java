package com.vokerg.taskplanner.api;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vokerg.taskplanner.model.Task;
import com.vokerg.taskplanner.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskApi {

    @Autowired 
    TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getTasks() {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable String taskId) {
        return ResponseEntity.of(this.taskService.getTaskById(taskId));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task createdTask = task;

        if (createdTask.getId() == null || createdTask.getId().isBlank()) {
            createdTask.setId("task-" + Instant.now().toEpochMilli());
        }

        if (createdTask.getCreatedAt() == null) {
            createdTask.setCreatedAt(Instant.now());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Task>> getTasksByProjectId(@PathVariable String projectId) {
        return ResponseEntity.ok(this.taskService.getTasksForProject(projectId));
    }


}
