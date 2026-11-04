package com.example.taskmanager.repository;
import com.example.taskmanager.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UserRepository extends JpaRepository<AppUser,Long> {
 Optional<AppUser> findByEmailIgnoreCase(String email);
 boolean existsByEmailIgnoreCase(String email);
 Optional<AppUser> findByIdAndTenant_Id(Long id,Long tenantId);
 List<AppUser> findAllByTenant_Id(Long tenantId);
}
