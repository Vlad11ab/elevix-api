package mycode.onlineshopspring.integration;

import mycode.onlineshopspring.auth.dto.AuthResponse;
import mycode.onlineshopspring.auth.dto.LoginRequest;
import mycode.onlineshopspring.auth.dto.RegisterRequest;
import mycode.onlineshopspring.products.dto.ProductsListResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthFlowIntegrationTest {

    private static final String EMAIL = "auth-flow@test.ro";
    private static final String PASSWORD = "secret1234";

    @Autowired
    private TestRestTemplate restTemplate;

    private static String accessToken;

    @Test
    @Order(1)
    void register_newCustomer_returns201AndTokens() {
        // ARRANGE
        RegisterRequest request = new RegisterRequest(
                EMAIL, PASSWORD, "Test User",
                "Str. Test 1", "Str. Test 1", "Romania", "0700000001"
        );

        // ACT
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/api/auth/register", request, AuthResponse.class
        );

        // ASSERT
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().accessToken());
        assertNotNull(response.getBody().refreshToken());
        assertEquals(EMAIL, response.getBody().email());
        assertTrue(response.getBody().permissions().contains("PRODUCT_READ"));
    }

    @Test
    @Order(2)
    void login_withValidCredentials_returns200AndTokens() {
        // ARRANGE
        LoginRequest request = new LoginRequest(EMAIL, PASSWORD);

        // ACT
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/api/auth/login", request, AuthResponse.class
        );

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().accessToken());
        accessToken = response.getBody().accessToken();
    }

    @Test
    @Order(3)
    void protectedEndpoint_withoutToken_returns401or403() {
        // ACT
        ResponseEntity<String> response = restTemplate.getForEntity("/api/products", String.class);

        // ASSERT
        assertTrue(response.getStatusCode() == HttpStatus.UNAUTHORIZED
                || response.getStatusCode() == HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(4)
    void protectedEndpoint_withToken_returns200() {
        // ARRANGE
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        // ACT
        ResponseEntity<ProductsListResponse> response = restTemplate.exchange(
                "/api/products", HttpMethod.GET, new HttpEntity<>(headers), ProductsListResponse.class
        );

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(5)
    void writeEndpoint_asCustomer_returns403() {
        // ARRANGE — Customer doesn't have PRODUCT_WRITE permission
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        String json = "{\"sku\":\"X\",\"name\":\"X\",\"price\":1,\"weight\":1,\"descriptions\":\"X\",\"category\":\"X\",\"stock\":1}";

        // ACT
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/products", HttpMethod.POST, new HttpEntity<>(json, headers), String.class
        );

        // ASSERT
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
