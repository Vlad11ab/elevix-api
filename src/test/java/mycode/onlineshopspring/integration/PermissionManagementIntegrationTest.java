package mycode.onlineshopspring.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mycode.onlineshopspring.auth.dto.AuthResponse;
import mycode.onlineshopspring.auth.dto.LoginRequest;
import mycode.onlineshopspring.auth.permission.dto.PermissionResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PermissionManagementIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String customerToken;

    @BeforeAll
    void loginSeededUsers() {
        // ARRANGE
        adminToken = login("admin@shop.com", "admin1234");
        customerToken = login("customer@shop.com", "customer1234");
    }

    private String login(String email, String password) {
        ResponseEntity<AuthResponse> resp = restTemplate.postForEntity(
                "/api/auth/login", new LoginRequest(email, password), AuthResponse.class);
        return resp.getBody().accessToken();
    }

    private HttpHeaders bearer(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    // === PERMISSION CATALOG ===

    @Test
    @Order(1)
    void admin_canListPermissions_returns200() {
        // ACT
        ResponseEntity<PermissionResponse[]> resp = restTemplate.exchange(
                "/api/permissions", HttpMethod.GET, new HttpEntity<>(bearer(adminToken)), PermissionResponse[].class);

        // ASSERT
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody().length >= 10, "should include all 10 seeded permissions");
    }

    @Test
    @Order(2)
    void customer_cannotListPermissions_returns403() {
        // ACT
        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/permissions", HttpMethod.GET, new HttpEntity<>(bearer(customerToken)), String.class);

        // ASSERT
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    @Test
    @Order(3)
    void admin_canCreateNewPermission_returns201() {
        // ARRANGE
        String json = "{\"name\":\"REPORT_READ\"}";

        // ACT
        ResponseEntity<PermissionResponse> resp = restTemplate.exchange(
                "/api/permissions", HttpMethod.POST, new HttpEntity<>(json, bearer(adminToken)), PermissionResponse.class);

        // ASSERT
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertNotNull(resp.getBody().id());
        assertEquals("REPORT_READ", resp.getBody().name());
    }

    @Test
    @Order(4)
    void admin_creatingDuplicatePermission_returns409() {
        // ARRANGE
        String json = "{\"name\":\"USER_READ\"}";

        // ACT
        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/permissions", HttpMethod.POST, new HttpEntity<>(json, bearer(adminToken)), String.class);

        // ASSERT
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    @Order(5)
    void admin_creatingInvalidPermissionName_returns400() {
        // ARRANGE — lowercase, fails the UPPER_SNAKE_CASE pattern
        String json = "{\"name\":\"lowercase\"}";

        // ACT
        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/permissions", HttpMethod.POST, new HttpEntity<>(json, bearer(adminToken)), String.class);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    // === USER PERMISSIONS ===

    @Test
    @Order(6)
    void admin_canGrantNewPermissionToCustomer_returns200() throws Exception {
        // ARRANGE — find customer's user UUID via /api/users
        String customerUserId = findUserIdByEmail("customer@shop.com");

        // ACT
        String json = "{\"name\":\"USER_READ\"}";
        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/users/" + customerUserId + "/permissions", HttpMethod.POST,
                new HttpEntity<>(json, bearer(adminToken)), String.class);

        // ASSERT
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        List<String> permissions = objectMapper.readValue(resp.getBody(), new TypeReference<>() {});
        assertTrue(permissions.contains("USER_READ"), "customer should now have USER_READ");
    }

    @Test
    @Order(7)
    void admin_canRevokePermissionFromCustomer_returns200() throws Exception {
        // ARRANGE
        String customerUserId = findUserIdByEmail("customer@shop.com");

        // ACT
        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/users/" + customerUserId + "/permissions/USER_READ", HttpMethod.DELETE,
                new HttpEntity<>(bearer(adminToken)), String.class);

        // ASSERT
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        List<String> permissions = objectMapper.readValue(resp.getBody(), new TypeReference<>() {});
        assertTrue(!permissions.contains("USER_READ"), "USER_READ should have been removed");
    }

    @Test
    @Order(8)
    void customer_cannotManageUserPermissions_returns403() {
        // ACT
        String json = "{\"name\":\"USER_READ\"}";
        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/users/00000000-0000-0000-0000-000000000000/permissions",
                HttpMethod.POST, new HttpEntity<>(json, bearer(customerToken)), String.class);

        // ASSERT
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    @Test
    @Order(9)
    void admin_grantingUnknownPermission_returns404() {
        // ARRANGE
        String customerUserId = findUserIdByEmail("customer@shop.com");

        // ACT
        String json = "{\"name\":\"DOES_NOT_EXIST\"}";
        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/users/" + customerUserId + "/permissions",
                HttpMethod.POST, new HttpEntity<>(json, bearer(adminToken)), String.class);

        // ASSERT
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    @Order(10)
    void errorBody_isInEnglish() {
        // ACT
        ResponseEntity<Map> resp = restTemplate.exchange(
                "/api/permissions", HttpMethod.GET,
                new HttpEntity<>(bearer(customerToken)), Map.class);

        // ASSERT
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
        String message = resp.getBody().get("message").toString();
        assertTrue(message.toLowerCase().contains("permission") || message.toLowerCase().contains("operation"),
                "message should be in English");
    }

    private String findUserIdByEmail(String email) {
        try {
            ResponseEntity<String> resp = restTemplate.exchange(
                    "/api/users", HttpMethod.GET, new HttpEntity<>(bearer(adminToken)), String.class);
            assertEquals(HttpStatus.OK, resp.getStatusCode());
            List<Map<String, Object>> users = objectMapper.readValue(resp.getBody(), new TypeReference<>() {});
            return users.stream()
                    .filter(u -> email.equals(u.get("email")))
                    .map(u -> (String) u.get("id"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Seeded user not found: " + email));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
