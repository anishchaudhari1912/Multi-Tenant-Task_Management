package com.example.taskmanager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    private String adminToken;
    private String suffix;

    @BeforeEach
    void registerTenant() throws Exception {
        suffix = UUID.randomUUID().toString().substring(0, 8);
        String email = "admin" + suffix + "@example.com";
        mvc.perform(post("/tenant/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"tenantName\":\"Org " + suffix + "\",\"adminEmail\":\"" + email + "\",\"password\":\"Admin@123\"}"))
                .andExpect(status().isCreated());
        adminToken = login(email, "Admin@123");
    }

    @Test
    void fullAdminManagerUserFlowWorks() throws Exception {
        String managerEmail = "manager" + suffix + "@example.com";
        String userEmail = "user" + suffix + "@example.com";
        mvc.perform(post("/users").header("Authorization", bearer(adminToken)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + managerEmail + "\",\"password\":\"Manager@123\",\"role\":\"MANAGER\"}"))
                .andExpect(status().isCreated());
        mvc.perform(post("/users").header("Authorization", bearer(adminToken)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + userEmail + "\",\"password\":\"Userpass@123\",\"role\":\"USER\"}"))
                .andExpect(status().isCreated());
        JsonNode users = mapper.readTree(mvc.perform(get("/users").header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        long userId = -1;
        for (JsonNode item : users) if (userEmail.equals(item.get("email").asText())) userId = item.get("id").asLong();
        String managerToken = login(managerEmail, "Manager@123");
        JsonNode task = mapper.readTree(mvc.perform(post("/tasks").header("Authorization", bearer(managerToken))
                .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Prepare release\",\"description\":\"First task\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
        long taskId = task.get("id").asLong();
        mvc.perform(put("/tasks/{id}/assign", taskId).param("userId", String.valueOf(userId)).header("Authorization", bearer(managerToken)))
                .andExpect(status().isOk());
        String userToken = login(userEmail, "Userpass@123");
        mvc.perform(get("/tasks").header("Authorization", bearer(userToken))).andExpect(status().isOk()).andExpect(jsonPath("$[0].title").value("Prepare release"));
        mvc.perform(put("/tasks/{id}/status", taskId).header("Authorization", bearer(userToken)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"DONE\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void validationReturnsBadRequest() throws Exception {
        mvc.perform(post("/users").header("Authorization", bearer(adminToken)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"bad-email\",\"password\":\"123\",\"role\":\"USER\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void protectedEndpointRequiresAuthentication() throws Exception {
        mvc.perform(get("/tasks")).andExpect(status().isUnauthorized());
    }

    @Test
    void invalidBearerTokenIsRejected() throws Exception {
        mvc.perform(get("/tasks").header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminCannotCreateTasks() throws Exception {
        mvc.perform(post("/tasks").header("Authorization", bearer(adminToken)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Task title\",\"description\":\"description\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void healthEndpointIsPublic() throws Exception {
        mvc.perform(get("/health")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void loginDoesNotRequireOrAcceptTenantId() throws Exception {
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin" + suffix + "@example.com\",\"password\":\"Admin@123\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.tenantId").isNumber());
    }


    @Test
    void managerCannotAccessAnotherTenantsTask() throws Exception {
        String managerAEmail = "managerA" + suffix + "@example.com";
        mvc.perform(post("/users").header("Authorization", bearer(adminToken)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + managerAEmail + "\",\"password\":\"Manager@123\",\"role\":\"MANAGER\"}"))
                .andExpect(status().isCreated());
        String managerAToken = login(managerAEmail, "Manager@123");
        JsonNode taskA = mapper.readTree(mvc.perform(post("/tasks").header("Authorization", bearer(managerAToken))
                .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Tenant A task\",\"description\":\"private\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());

        String otherSuffix = UUID.randomUUID().toString().substring(0, 8);
        String otherAdmin = "admin" + otherSuffix + "@example.com";
        mvc.perform(post("/tenant/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"tenantName\":\"Other " + otherSuffix + "\",\"adminEmail\":\"" + otherAdmin + "\",\"password\":\"Admin@123\"}"))
                .andExpect(status().isCreated());
        String otherAdminToken = login(otherAdmin, "Admin@123");
        String managerBEmail = "managerB" + otherSuffix + "@example.com";
        mvc.perform(post("/users").header("Authorization", bearer(otherAdminToken)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + managerBEmail + "\",\"password\":\"Manager@123\",\"role\":\"MANAGER\"}"))
                .andExpect(status().isCreated());
        String managerBToken = login(managerBEmail, "Manager@123");
        mvc.perform(put("/tasks/{id}/assign", taskA.get("id").asLong()).param("userId", "1")
                .header("Authorization", bearer(managerBToken))).andExpect(status().isNotFound());
        mvc.perform(get("/tasks").header("Authorization", bearer(managerBToken)))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    private String login(String email, String password) throws Exception {
        String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        String response = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(response).get("token").asText();
    }
    private String bearer(String token) { return "Bearer " + token; }
}
