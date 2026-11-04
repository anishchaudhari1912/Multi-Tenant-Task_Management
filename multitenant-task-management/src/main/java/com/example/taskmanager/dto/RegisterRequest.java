package com.example.taskmanager.dto;
import jakarta.validation.constraints.*;
public record RegisterRequest(@NotBlank @Size(min=2,max=100) String tenantName,@NotBlank @Email @Size(max=150) String adminEmail,@NotBlank @Size(min=8,max=72) String password) {}
