package com.example.taskmanager.dto;
import com.example.taskmanager.entity.Task;
public record TaskResponse(Long id,String title,String description,String status,Long assignedUserId,String assignedUserEmail) {
 public static TaskResponse from(Task task){return new TaskResponse(task.getId(),task.getTitle(),task.getDescription(),task.getStatus().name(),task.getAssignedTo()==null?null:task.getAssignedTo().getId(),task.getAssignedTo()==null?null:task.getAssignedTo().getEmail());}
}
