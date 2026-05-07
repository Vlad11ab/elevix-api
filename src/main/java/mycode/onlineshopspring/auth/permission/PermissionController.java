package mycode.onlineshopspring.auth.permission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.auth.permission.dto.CreatePermissionRequest;
import mycode.onlineshopspring.auth.permission.dto.PermissionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Permissions", description = "Admin-only catalog of permissions. Use with /api/users/{id}/permissions to grant or revoke access.")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    @Operation(summary = "List all permissions in the catalog (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing PERMISSION_READ permission")
    })
    public ResponseEntity<List<PermissionResponse>> list() {
        return ResponseEntity.ok(permissionService.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_WRITE')")
    @Operation(summary = "Create a new permission in the catalog (ADMIN)",
            description = "Adds a custom permission. Use UPPER_SNAKE_CASE (e.g. REPORT_READ).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Permission created"),
            @ApiResponse(responseCode = "400", description = "Invalid name format"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing PERMISSION_WRITE permission"),
            @ApiResponse(responseCode = "409", description = "Permission already exists")
    })
    public ResponseEntity<PermissionResponse> create(@Valid @RequestBody CreatePermissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.create(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_WRITE')")
    @Operation(summary = "Delete a permission from the catalog (ADMIN)",
            description = "Removes a permission. Any user that holds this permission will lose it (cascade through the join table).")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Permission deleted"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing PERMISSION_WRITE permission"),
            @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
