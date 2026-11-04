package com.example.taskmanager.controller;
import com.example.taskmanager.dto.*;
import com.example.taskmanager.entity.*;
import com.example.taskmanager.security.TenantContext;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/tasks")
public class TaskController
{
 private final TaskService service;
 private final UserRepository users;
 public TaskController(TaskService service,UserRepository users)
 {
  this.service=service;this.users=users;
 }
 @PostMapping @PreAuthorize("hasRole('MANAGER')")
 public ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request)
 {
  return ResponseEntity.status(HttpStatus.CREATED)
          .body(TaskResponse.from(service.create(request,TenantContext.getTenantId())));
 }
 @PutMapping("/{id}/assign") @PreAuthorize("hasRole('MANAGER')")
 public TaskResponse assign(@PathVariable Long id,@RequestParam Long userId)
 {
  return TaskResponse.from(service.assign(id,userId,TenantContext.getTenantId()));
 }
 @PutMapping("/{id}/status") @PreAuthorize("hasRole('USER')")
 public TaskResponse status(@PathVariable Long id,@Valid @RequestBody StatusRequest request,Authentication authentication)
 {
  return TaskResponse.from(service.updateStatus(id,request.status(),TenantContext.getTenantId(),currentUserId(authentication)));
 }
 @GetMapping @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
 public List<TaskResponse> list(Authentication authentication)
 {
  Role role=Role.valueOf(authentication.getAuthorities().iterator().next().getAuthority().substring(5));
  return service.list(TenantContext.getTenantId(),role,currentUserId(authentication));
 }
 private Long currentUserId(Authentication authentication)
 {
  return users.findByEmailIgnoreCase(authentication.getName()).orElseThrow().getId();
 }
}
