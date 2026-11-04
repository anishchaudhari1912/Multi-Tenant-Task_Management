package com.example.taskmanager.entity;
import jakarta.persistence.*;
@Entity
@Table(name="app_users", uniqueConstraints=@UniqueConstraint(columnNames="email"))
public class AppUser {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
 private Long id;
 @Column(nullable=false,unique=true,length=150)
 private String email;
 @Column(nullable=false)
 private String password;
 @Enumerated(EnumType.STRING) @Column(nullable=false)
 private Role role;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="tenant_id",nullable=false)
 private Tenant tenant;
 protected AppUser() {}
 public AppUser(String email,String password,Role role,Tenant tenant)
 {
  this.email=email;
  this.password=password;
  this.role=role;
  this.tenant=tenant;
 }
 public Long getId()
 {
  return id;
 }
 public String getEmail()
 {
  return email;
 }
 public String getPassword()
 {
  return password;
 }
 public Role getRole()
 {
  return role;
 }
 public Tenant getTenant()
 {
  return tenant;
 }
}
