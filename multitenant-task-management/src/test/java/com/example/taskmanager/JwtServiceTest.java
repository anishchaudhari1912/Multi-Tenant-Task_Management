package com.example.taskmanager;
import com.example.taskmanager.entity.*;import com.example.taskmanager.security.JwtService;import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.util.ReflectionTestUtils;
class JwtServiceTest {
 private final JwtService jwt=new JwtService("this-is-a-long-development-secret-key-for-tests",3600000);
 @Test void tokenContainsTenantAndRole(){Tenant tenant=new Tenant("Org");ReflectionTestUtils.setField(tenant,"id",1L);AppUser user=new AppUser("a@b.com","encoded",Role.ADMIN,tenant);String token=jwt.generateToken(user);assertEquals("a@b.com",jwt.extractEmail(token));assertEquals("ADMIN",jwt.extractClaims(token).get("role"));assertNotNull(jwt.extractClaims(token).get("tenant_id"));}
 @Test void rejectsDifferentEmail(){Tenant tenant=new Tenant("Org");ReflectionTestUtils.setField(tenant,"id",1L);AppUser user=new AppUser("a@b.com","encoded",Role.USER,tenant);assertFalse(jwt.isTokenValid(jwt.generateToken(user),"other@b.com"));}
 @Test void rejectsMalformedToken(){assertFalse(jwt.isTokenValid("not-a-token","a@b.com"));}
}
