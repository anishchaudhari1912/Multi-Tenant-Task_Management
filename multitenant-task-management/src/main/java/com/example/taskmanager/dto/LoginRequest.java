package com.example.taskmanager.dto;
import jakarta.validation.constraints.*;
public record LoginRequest(@NotBlank @Email @Size(max=150) String email,@NotBlank String password) {}
