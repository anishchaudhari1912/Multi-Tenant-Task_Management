package com.example.taskmanager;

import com.example.taskmanager.dto.CreateUserRequest;
import com.example.taskmanager.entity.*;
import com.example.taskmanager.exception.BadRequestException;
import com.example.taskmanager.repository.*;
import com.example.taskmanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock UserRepository users;
    @Mock TenantRepository tenants;
    @Mock PasswordEncoder encoder;
    @InjectMocks UserService service;

    @Test void createsUserForCurrentTenant() {
        Tenant tenant = new Tenant("Example");
        when(users.existsByEmailIgnoreCase("person@example.com")).thenReturn(false);
        when(tenants.findById(7L)).thenReturn(Optional.of(tenant));
        when(encoder.encode("Password@1")).thenReturn("encoded");
        when(users.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
        AppUser saved = service.create(new CreateUserRequest("person@example.com", "Password@1", Role.USER), 7L);
        assertEquals(Role.USER, saved.getRole());
        assertEquals(tenant, saved.getTenant());
        assertEquals("encoded", saved.getPassword());
    }

    @Test void rejectsDuplicateEmail() {
        when(users.existsByEmailIgnoreCase("person@example.com")).thenReturn(true);
        assertThrows(BadRequestException.class, () -> service.create(new CreateUserRequest("person@example.com", "Password@1", Role.USER), 7L));
        verify(tenants, never()).findById(anyLong());
    }

    @Test void rejectsCreatingAnotherAdmin() {
        assertThrows(BadRequestException.class, () -> service.create(new CreateUserRequest("person@example.com", "Password@1", Role.ADMIN), 7L));
    }
}
