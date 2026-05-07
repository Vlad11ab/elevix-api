package mycode.onlineshopspring.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.auth.dto.AuthResponse;
import mycode.onlineshopspring.auth.dto.LoginRequest;
import mycode.onlineshopspring.auth.dto.RefreshRequest;
import mycode.onlineshopspring.auth.dto.RegisterRequest;
import mycode.onlineshopspring.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Public endpoints: register a customer account, login, and refresh JWT tokens.")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new customer account",
            description = "Creates a User + Customer profile with default customer permissions (PRODUCT_READ, ORDER_*, ORDER_DETAILS_*).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created; access + refresh tokens returned"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password",
            description = "Returns an access token (15 min) and a refresh token (7 days). Use the access token in `Authorization: Bearer <token>` for further calls.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Bad credentials")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Get a new access token from a refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "New tokens returned"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}
