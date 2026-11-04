package com.example.taskmanager.dto;
import com.example.taskmanager.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;
public record StatusRequest(@NotNull TaskStatus status) {}
