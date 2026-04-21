package com.vokerg.taskplanner.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vokerg.taskplanner.dto.ChangeTaskStatusRequest;
import com.vokerg.taskplanner.dto.TaskResponse;
import com.vokerg.taskplanner.dto.UpdateTaskRequest;
import com.vokerg.taskplanner.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskApi {

    private final TaskService taskService;

    public TaskApi(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable String taskId) {
        return ResponseEntity.of(this.taskService.getTaskById(taskId));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable String taskId) {
        this.taskService.removeTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> replaceTask(@PathVariable String taskId, @Valid @RequestBody UpdateTaskRequest task) {
        return ResponseEntity.of(this.taskService.replaceTask(taskId, task));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable String taskId, @Valid @RequestBody ChangeTaskStatusRequest request) {
        return ResponseEntity.of(this.taskService.changeTaskStatus(taskId, request));
    }
}
