package com.vokerg.taskplanner.model;

import java.time.Instant;

import lombok.Data;

@Data
public class Project {
    private String id;
    private String title;
    private String description;
    private Instant createdAt;
    private boolean completed;
    
}
