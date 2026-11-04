package com.example.taskmanager.repository;
import com.example.taskmanager.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TenantRepository extends JpaRepository<Tenant,Long>
{
    boolean existsByNameIgnoreCase(String name);
}
