package com.example.taskmanager.service;
import com.example.taskmanager.dto.CreateUserRequest;
import com.example.taskmanager.entity.*;
import com.example.taskmanager.exception.BadRequestException;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class UserService {
 private final UserRepository users;
 private final TenantRepository tenants;
 private final PasswordEncoder encoder;
 public UserService(UserRepository users,TenantRepository tenants,PasswordEncoder encoder)
 {
  this.users=users;this.tenants=tenants;this.encoder=encoder;
 }
 @Transactional public AppUser create(CreateUserRequest request,Long tenantId)
 {
  if(request.role()==Role.ADMIN)throw new BadRequestException("Only one tenant administrator is created during registration");
  if(users.existsByEmailIgnoreCase(request.email()))throw new BadRequestException("Email already exists");
  Tenant tenant=tenants.findById(tenantId).orElseThrow(()->new ResourceNotFoundException("Tenant not found"));
  return users.save(new AppUser(request.email().trim().toLowerCase(),encoder.encode(request.password()),request.role(),tenant));
 }
 public List<AppUser> list(Long tenantId)
 {
  return users.findAllByTenant_Id(tenantId);
 }
}
