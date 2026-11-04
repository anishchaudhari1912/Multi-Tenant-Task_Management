package com.example.taskmanager.dto;
public record AuthResponse(String token,String tokenType,long userId,String email,String role,long tenantId) {}
