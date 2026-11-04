package com.example.taskmanager.service;
import com.example.taskmanager.dto.*;
import com.example.taskmanager.entity.*;
import com.example.taskmanager.exception.BadRequestException;
import com.example.taskmanager.repository.*;
import com.example.taskmanager.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class AuthService {
 private final TenantRepository tenants;
 private final UserRepository users;
 private final PasswordEncoder encoder;
 private final AuthenticationManager authManager;
 private final JwtService jwt;
 public AuthService(TenantRepository tenants,UserRepository users,PasswordEncoder encoder,AuthenticationManager authManager,JwtService jwt)
 {
  this.tenants=tenants;
  this.users=users;
  this.encoder=encoder;
  this.authManager=authManager;
  this.jwt=jwt;
 }
 @Transactional public void register(RegisterRequest request)
 {
  if(tenants.existsByNameIgnoreCase(request.tenantName()))throw new BadRequestException("Tenant name already exists");
  if(users.existsByEmailIgnoreCase(request.adminEmail()))throw new BadRequestException("Email already exists");
  Tenant tenant=tenants.save(new Tenant(request.tenantName().trim()));users.save(new AppUser(request.adminEmail().trim().toLowerCase(),encoder.encode(request.password()),Role.ADMIN,tenant));
 }
 public AuthResponse login(LoginRequest request)
 {
  authManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(),request.password()));
  AppUser user=users.findByEmailIgnoreCase(request.email()).orElseThrow(()->new BadCredentialsException("Invalid credentials"));
  return new AuthResponse(jwt.generateToken(user),"Bearer",user.getId(),user.getEmail(),user.getRole().name(),user.getTenant().getId());
 }
}
