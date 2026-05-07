package mycode.onlineshopspring.customer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.customer.dto.CustomerDto;
import mycode.onlineshopspring.customer.dto.CustomerListResponse;
import mycode.onlineshopspring.customer.dto.CustomerResponse;
import mycode.onlineshopspring.customer.service.UserCommandService;
import mycode.onlineshopspring.customer.service.UserQuerryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Customers", description = "Customer profiles. Listing all customers is admin-only; each user can manage own profile via /me.")
public class CustomerController {

    private final UserQuerryService userQuerryService;
    private final UserCommandService userCommandService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    @Operation(summary = "List all customers (ADMIN)",
            description = "Returns a paginated list of all customers. Requires permission `USER_READ` (admin only).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @ApiResponse(responseCode = "403", description = "Missing USER_READ permission")
    })
    public ResponseEntity<CustomerListResponse> getAllCustomers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userQuerryService.findAllCustomers(page, size));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my profile",
            description = "Returns the profile of the currently authenticated user. Any authenticated caller is allowed.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<CustomerResponse> getMyProfile(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(userQuerryService.findCustomerByEmail(principal.getUsername()));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update my profile",
            description = "Updates the profile of the currently authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<CustomerResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody CustomerDto dto) {
        return ResponseEntity.ok(userCommandService.updateCustomerByEmail(principal.getUsername(), dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ') or @securityService.isCustomerOwner(#id)")
    @Operation(summary = "Get customer by ID (ADMIN or owner)",
            description = "Returns a customer. Allowed to admins (`USER_READ`) or to the owner of the profile.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Not admin and not owner"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable UUID id) {
        return ResponseEntity.ok(userQuerryService.findCustomerById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    @Operation(summary = "Update customer profile by ID (ADMIN)",
            description = "Admin-only update for any customer profile. Use `/me` for self-service.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing USER_WRITE permission"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable UUID id, @Valid @RequestBody CustomerDto dto) {
        return ResponseEntity.ok(userCommandService.updateCustomer(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    @Operation(summary = "Delete a customer (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Customer deleted"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing USER_WRITE permission"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        userCommandService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
