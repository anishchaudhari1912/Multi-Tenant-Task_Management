package com.example.taskmanager.dto;
import com.example.taskmanager.entity.Role;
import jakarta.validation.constraints.*;
public record CreateUserRequest(@NotBlank @Email @Size(max=150) String email,@NotBlank @Size(min=8,max=72) String password,@NotNull Role role) {}
