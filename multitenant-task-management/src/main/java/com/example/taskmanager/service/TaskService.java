package com.example.taskmanager.service;
import com.example.taskmanager.dto.*;
import com.example.taskmanager.entity.*;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class TaskService
{
 private final TaskRepository tasks;
 private final TenantRepository tenants;
 private final UserRepository users;
 public TaskService(TaskRepository tasks,TenantRepository tenants,UserRepository users)
 {
  this.tasks=tasks;
  this.tenants=tenants;
  this.users=users;
 }
 @Transactional public Task create(CreateTaskRequest request,Long tenantId)
 {
  Tenant tenant=tenants.findById(tenantId)
          .orElseThrow(()->new ResourceNotFoundException("Tenant not found"));
  return tasks.save(new Task(request.title().trim(),request.description(),tenant));
 }
 @Transactional public Task assign(Long taskId,Long userId,Long tenantId)
 {
  Task task=tasks.findByIdAndTenant_Id(taskId,tenantId)
          .orElseThrow(()->new ResourceNotFoundException("Task not found in your organization"));
  AppUser user=users.findByIdAndTenant_Id(userId,tenantId)
          .orElseThrow(()->new ResourceNotFoundException("User not found in your organization"));
  task.assign(user);return tasks.save(task);
 }
 @Transactional public Task updateStatus(Long taskId,TaskStatus status,Long tenantId,Long userId)
 {
  Task task=tasks.findByIdAndTenant_IdAndAssignedTo_Id(taskId,tenantId,userId)
          .orElseThrow(()->new ResourceNotFoundException("Assigned task not found"));
  task.updateStatus(status);
  return tasks.save(task);
 }
 @Transactional(readOnly=true) public List<TaskResponse> list(Long tenantId,Role role,Long userId)
 {
  List<Task> result=role==Role.USER?tasks.findAllByTenant_IdAndAssignedTo_Id(tenantId,userId):tasks.findAllByTenant_Id(tenantId);
  return result.stream().map(TaskResponse::from).toList();
 }
}
