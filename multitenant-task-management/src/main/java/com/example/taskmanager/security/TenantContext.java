package com.example.taskmanager.security;
public final class TenantContext {
 private static final ThreadLocal<Long> TENANT_ID=new ThreadLocal<>();
 private TenantContext(){}
 public static void setTenantId(Long id)
 {
  TENANT_ID.set(id);
 }
 public static Long getTenantId()
 {
  return TENANT_ID.get();
 }
 public static void clear()
 {
  TENANT_ID.remove();
 }
}
