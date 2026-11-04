package com.example.taskmanager.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="tenants")
public class Tenant {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
 private Long id;
 @Column(nullable=false,unique=true,length=100)
 private String name;
 @Column(nullable=false)
 private LocalDateTime createdAt;
 protected Tenant() {}
 public Tenant(String name)
 { this.name=name;
  this.createdAt=LocalDateTime.now();
 }
 public Long getId()
 {
  return id;
 }
 public String getName()
 {
  return name;
 }
 public LocalDateTime getCreatedAt()
 {
  return createdAt;
 }
}
