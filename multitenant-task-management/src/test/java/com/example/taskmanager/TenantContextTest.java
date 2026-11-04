package com.example.taskmanager;
import com.example.taskmanager.security.TenantContext;import org.junit.jupiter.api.*;import static org.junit.jupiter.api.Assertions.*;
class TenantContextTest {
 @AfterEach void clean(){TenantContext.clear();}
 @Test void storesAndReadsTenant(){TenantContext.setTenantId(12L);assertEquals(12L,TenantContext.getTenantId());}
 @Test void clearRemovesTenant(){TenantContext.setTenantId(12L);TenantContext.clear();assertNull(TenantContext.getTenantId());}
 @Test void startsEmpty(){assertNull(TenantContext.getTenantId());}
}
