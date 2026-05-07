package mycode.onlineshopspring.auth.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.auth.user.dto.AssignPermissionRequest;
import mycode.onlineshopspring.auth.user.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "Admin-only — list users, inspect their permissions, grant or revoke access.")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    @Operation(summary = "List all users with their permissions (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing USER_READ permission")
    })
    public ResponseEntity<List<UserResponse>> list() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_READ')")
    @Operation(summary = "Get a user by ID with permissions (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing USER_READ permission"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> get(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.findById(userId));
    }

    @GetMapping("/{userId}/permissions")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    @Operation(summary = "List permissions held by a user (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permissions returned"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing PERMISSION_READ permission"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<String>> listPermissions(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.listPermissions(userId));
    }

    @PostMapping("/{userId}/permissions")
    @PreAuthorize("hasAuthority('PERMISSION_WRITE')")
    @Operation(summary = "Grant a permission to a user (ADMIN)",
            description = "Adds the named permission to the user's set. Idempotent — granting an already-held permission has no effect.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated permission set returned"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing PERMISSION_WRITE permission"),
            @ApiResponse(responseCode = "404", description = "User or permission not found")
    })
    public ResponseEntity<List<String>> assign(@PathVariable UUID userId,
                                               @Valid @RequestBody AssignPermissionRequest request) {
        return ResponseEntity.ok(userService.assignPermission(userId, request.name().trim()));
    }

    @DeleteMapping("/{userId}/permissions/{permissionName}")
    @PreAuthorize("hasAuthority('PERMISSION_WRITE')")
    @Operation(summary = "Revoke a permission from a user (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated permission set returned"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing PERMISSION_WRITE permission"),
            @ApiResponse(responseCode = "404", description = "User not found, or user does not hold this permission")
    })
    public ResponseEntity<List<String>> revoke(@PathVariable UUID userId,
                                               @PathVariable String permissionName) {
        return ResponseEntity.ok(userService.revokePermission(userId, permissionName));
    }
}
