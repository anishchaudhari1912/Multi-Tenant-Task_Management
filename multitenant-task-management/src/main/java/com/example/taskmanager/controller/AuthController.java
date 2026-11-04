package com.example.taskmanager.controller;
import com.example.taskmanager.dto.*;
import com.example.taskmanager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping
public class AuthController
{
 private final AuthService service;
 public AuthController(AuthService service)
 {
  this.service=service;
 }
 @PostMapping("/tenant/register")
 public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request)
 {
  service.register(request);
  return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Tenant and administrator registered successfully"));}
 @PostMapping("/auth/login")
 public AuthResponse login(@Valid @RequestBody LoginRequest request)
 {
  return service.login(request);
 }
}
