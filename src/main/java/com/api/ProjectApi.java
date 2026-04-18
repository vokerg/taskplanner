package com.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vokerg.taskplanner.model.Project;

@RestController
@RequestMapping("/api/projects")
public class ProjectApi {
    @GetMapping
    public ResponseEntity<List<Project>> getProjects() {
        return ResponseEntity.ok(List.of());
    }
}
