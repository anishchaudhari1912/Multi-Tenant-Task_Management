package com.example.taskmanager.controller;
import com.example.taskmanager.dto.CreateUserRequest;
import com.example.taskmanager.entity.AppUser;
import com.example.taskmanager.security.TenantContext;
import com.example.taskmanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/users")
public class UserController
{
 private final UserService service;
 public UserController(UserService service)
 {
  this.service=service;
 }
 @PostMapping @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<Map<String,Object>> create(@Valid @RequestBody CreateUserRequest request)
 {
  AppUser user=service.create(request,TenantContext.getTenantId());
  return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id",user.getId(),"email",user.getEmail(),"role",user.getRole().name()));
 }
 @GetMapping @PreAuthorize("hasRole('ADMIN')")
 public List<Map<String,Object>> list()
 {
  return service.list(TenantContext.getTenantId()).stream()
          .map(u->Map.<String,Object>of("id",u.getId(),"email",u.getEmail(),"role",u.getRole().name())).toList();
 }
}
