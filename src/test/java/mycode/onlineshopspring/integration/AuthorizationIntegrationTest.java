package mycode.onlineshopspring.integration;

import mycode.onlineshopspring.auth.dto.AuthResponse;
import mycode.onlineshopspring.auth.dto.LoginRequest;
import mycode.onlineshopspring.customer.dto.CustomerResponse;
import mycode.onlineshopspring.orders.dto.OrdersListResponse;
import mycode.onlineshopspring.products.dto.ProductsListResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthorizationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String adminToken;
    private String customerToken;

    @BeforeAll
    void loginSeededUsers() {
        // ARRANGE — login both seeded accounts; the seeder runs in test profile
        adminToken = login("admin@shop.com", "admin1234");
        customerToken = login("customer@shop.com", "customer1234");
    }

    private String login(String email, String password) {
        ResponseEntity<AuthResponse> resp = restTemplate.postForEntity(
                "/api/auth/login", new LoginRequest(email, password), AuthResponse.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        return resp.getBody().accessToken();
    }

    private HttpHeaders bearer(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    // === ANONYMOUS ===

    @Test
    void anonymous_cannotAccessProducts_returns401() {
        // ACT
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/products", String.class);
        // ASSERT
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    void anonymous_canHitSwagger_returns200() {
        // ACT
        ResponseEntity<String> resp = restTemplate.getForEntity("/v3/api-docs", String.class);
        // ASSERT
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    // === ADMIN ===

    @Test
    void admin_canListAllCustomers_returns200() {
        // ACT
        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/customers", HttpMethod.GET, new HttpEntity<>(bearer(adminToken)), String.class);
        // ASSERT
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void admin_canListAllOrders_returns200() {
        // ACT
        ResponseEntity<OrdersListResponse> resp = restTemplate.exchange(
                "/api/orders", HttpMethod.GET, new HttpEntity<>(bearer(adminToken)), OrdersListResponse.class);
        // ASSERT
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void admin_canCreateProduct_returns201() {
        // ARRANGE
        String json = """
                {"sku":"SKU-A","name":"Admin Product","price":50,"weight":1,
                 "descriptions":"d","category":"c","createDate":"2024-01-01T00:00:00.000+00:00","stock":10}
                """;
        // ACT
        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/products", HttpMethod.POST, new HttpEntity<>(json, bearer(adminToken)), String.class);
        // ASSERT
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    }

    // === CUSTOMER ===

    @Test
    void customer_canReadProducts_returns200() {
        // ACT
        ResponseEntity<ProductsListResponse> resp = restTemplate.exchange(
                "/api/products", HttpMethod.GET, new HttpEntity<>(bearer(customerToken)), ProductsListResponse.class);
        // ASSERT
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void customer_cannotCreateProduct_returns403() {
        // ARRANGE
        String json = "{\"sku\":\"X\",\"name\":\"X\",\"price\":1,\"weight\":1,\"descriptions\":\"X\",\"category\":\"X\",\"stock\":1}";
        // ACT
        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/products", HttpMethod.POST, new HttpEntity<>(json, bearer(customerToken)), String.class);
        // ASSERT
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
        assertTrue(resp.getBody().contains("yourPermissions"),
                "403 body should expose the caller's current permissions");
    }

    @Test
    void customer_cannotListAllCustomers_returns403() {
        // ACT
        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/customers", HttpMethod.GET, new HttpEntity<>(bearer(customerToken)), String.class);
        // ASSERT
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    @Test
    void customer_cannotListAllOrders_returns403() {
        // ACT
        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/orders", HttpMethod.GET, new HttpEntity<>(bearer(customerToken)), String.class);
        // ASSERT
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    @Test
    void customer_canSeeOwnProfileViaMe_returns200() {
        // ACT
        ResponseEntity<CustomerResponse> resp = restTemplate.exchange(
                "/api/customers/me", HttpMethod.GET, new HttpEntity<>(bearer(customerToken)), CustomerResponse.class);
        // ASSERT
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("customer@shop.com", resp.getBody().email());
    }

    @Test
    void customer_canSeeOwnOrdersViaMe_returns200() {
        // ACT
        ResponseEntity<OrdersListResponse> resp = restTemplate.exchange(
                "/api/orders/me", HttpMethod.GET, new HttpEntity<>(bearer(customerToken)), OrdersListResponse.class);
        // ASSERT
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
    }

    @Test
    void customer_cannotChangeOrderStatus_returns403() {
        // ARRANGE
        String json = "{\"status\":\"Shipped\"}";
        // ACT — picks a fake UUID; admin perm check happens before resource lookup
        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/orders/00000000-0000-0000-0000-000000000000/status",
                HttpMethod.PATCH, new HttpEntity<>(json, bearer(customerToken)), String.class);
        // ASSERT
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    // === ERROR PAYLOAD SHAPE ===

    @Test
    void forbiddenResponse_includesMissingPermissionContext() {
        // ACT — customer hitting an admin-only endpoint
        ResponseEntity<Map> resp = restTemplate.exchange(
                "/api/customers", HttpMethod.GET, new HttpEntity<>(bearer(customerToken)), Map.class);
        // ASSERT
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
        Map<String, Object> body = resp.getBody();
        assertNotNull(body);
        assertEquals(403, body.get("status"));
        assertNotNull(body.get("yourPermissions"), "should list caller's current permissions");
        assertNotNull(body.get("path"), "should include the request path");
    }

    @Test
    void unauthorizedResponse_hasClearMessage() {
        // ACT — no token
        ResponseEntity<Map> resp = restTemplate.exchange(
                "/api/products", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Map.class);
        // ASSERT
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        Map<String, Object> body = resp.getBody();
        assertNotNull(body);
        assertEquals(401, body.get("status"));
        assertTrue(body.get("message").toString().toLowerCase().contains("authentication") ||
                        body.get("message").toString().contains("Authorization"),
                "401 message should mention authentication");
    }
}
