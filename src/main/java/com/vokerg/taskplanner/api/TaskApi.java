package com.vokerg.taskplanner.api;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
        Task createdTask = this.taskService.createTask(task);
        return ResponseEntity
            .created(URI.create("/api/tasks/" + createdTask.getId()))
            .body(createdTask);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Task>> getTasksByProjectId(@PathVariable String projectId) {
        return ResponseEntity.ok(this.taskService.getTasksForProject(projectId));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable String taskId) {
        this.taskService.removeTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
