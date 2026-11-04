package com.example.taskmanager.entity;
import jakarta.persistence.*;
@Entity @Table(name="tasks")
public class Task {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
 private Long id;
 @Column(nullable=false,length=120)
 private String title;
 @Column(length=1000)
 private String description;
 @Enumerated(EnumType.STRING) @Column(nullable=false)
 private TaskStatus status;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="tenant_id",nullable=false)
 private Tenant tenant;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="assigned_to")
 private AppUser assignedTo;
 protected Task() {}
 public Task(String title,String description,Tenant tenant)
 {
  this.title=title;
  this.description=description;
  this.tenant=tenant;
  this.status=TaskStatus.TODO;}
 public void assign(AppUser user)
 {
  this.assignedTo=user;
 }
 public void updateStatus(TaskStatus status)
 {
  this.status=status;
 }
 public Long getId()
 {
  return id;
 }
 public String getTitle()
 {
  return title;
 }
 public String getDescription()
 {
  return description;
 }
 public TaskStatus getStatus()
 {
  return status;
 }
 public Tenant getTenant()
 {
  return tenant;
 }
 public AppUser getAssignedTo()
 {
  return assignedTo;
 }
}
