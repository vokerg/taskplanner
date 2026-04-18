package com.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/taskplanner")
public class TaskPlanner {

    @GetMapping
    public String getStatus() {
        return "TaskPlanner API is running";
    }
}
