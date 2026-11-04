package com.example.taskmanager.repository;
import com.example.taskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface TaskRepository extends JpaRepository<Task,Long> {
 Optional<Task> findByIdAndTenant_Id(Long id,Long tenantId);
 List<Task> findAllByTenant_Id(Long tenantId);
 List<Task> findAllByTenant_IdAndAssignedTo_Id(Long tenantId,Long userId);
 Optional<Task> findByIdAndTenant_IdAndAssignedTo_Id(Long id,Long tenantId,Long userId);
}
