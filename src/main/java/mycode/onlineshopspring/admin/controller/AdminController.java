package mycode.onlineshopspring.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.admin.dto.AdminResponse;
import mycode.onlineshopspring.admin.dto.CreateAdminRequest;
import mycode.onlineshopspring.admin.dto.UpdateAdminRequest;
import mycode.onlineshopspring.admin.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admins", description = "Admin staff profiles. Listing and creating admins is admin-only; admins manage their own profile via /me.")
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    @Operation(summary = "List all admin profiles (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing USER_READ permission")
    })
    public ResponseEntity<List<AdminResponse>> list() {
        return ResponseEntity.ok(adminService.findAll());
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my admin profile",
            description = "Returns the admin profile of the authenticated user. 404 if the caller is not an admin.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Caller has no admin profile")
    })
    public ResponseEntity<AdminResponse> me(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(adminService.findByEmail(principal.getUsername()));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update my admin profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Caller has no admin profile")
    })
    public ResponseEntity<AdminResponse> updateMe(@AuthenticationPrincipal UserDetails principal,
                                                  @Valid @RequestBody UpdateAdminRequest request) {
        return ResponseEntity.ok(adminService.updateByEmail(principal.getUsername(), request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    @Operation(summary = "Get admin by ID (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin found"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing USER_READ permission"),
            @ApiResponse(responseCode = "404", description = "Admin not found")
    })
    public ResponseEntity<AdminResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_WRITE')")
    @Operation(summary = "Create a new admin (ADMIN)",
            description = "Creates a User + Admin profile in one call. The new admin is granted ALL permissions by default.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Admin created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing USER_WRITE permission"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    public ResponseEntity<AdminResponse> create(@Valid @RequestBody CreateAdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    @Operation(summary = "Update admin profile by ID (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin updated"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing USER_WRITE permission"),
            @ApiResponse(responseCode = "404", description = "Admin not found")
    })
    public ResponseEntity<AdminResponse> update(@PathVariable UUID id,
                                                @Valid @RequestBody UpdateAdminRequest request) {
        return ResponseEntity.ok(adminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    @Operation(summary = "Delete an admin (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Admin deleted"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing USER_WRITE permission"),
            @ApiResponse(responseCode = "404", description = "Admin not found")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        adminService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
