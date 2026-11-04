package com.example.taskmanager.dto;
import jakarta.validation.constraints.*;
public record CreateTaskRequest(@NotBlank @Size(min=3,max=120) String title,@Size(max=1000) String description) {}
