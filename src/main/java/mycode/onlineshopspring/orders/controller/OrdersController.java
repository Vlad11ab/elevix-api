package mycode.onlineshopspring.orders.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.orders.dto.OrdersDto;
import mycode.onlineshopspring.orders.dto.OrdersListResponse;
import mycode.onlineshopspring.orders.dto.OrdersResponse;
import mycode.onlineshopspring.orders.service.OrdersCommandService;
import mycode.onlineshopspring.orders.service.OrdersQuerryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Orders", description = "Customers manage their own orders via /me; admins access /list.")
public class OrdersController {

    private final OrdersQuerryService ordersQuerryService;
    private final OrdersCommandService ordersCommandService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    @Operation(summary = "List all orders (ADMIN)",
            description = "Returns every order in the system. Requires `USER_READ`.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing USER_READ permission")
    })
    public ResponseEntity<OrdersListResponse> getAllOrders(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ordersQuerryService.findAllOrders(page, size));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ORDER_READ')")
    @Operation(summary = "List my orders",
            description = "Returns orders for the currently authenticated customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing ORDER_READ permission")
    })
    public ResponseEntity<OrdersListResponse> getMyOrders(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ordersQuerryService.findOrdersByEmail(principal.getUsername(), page, size));
    }

    @PostMapping("/me")
    @PreAuthorize("hasAuthority('ORDER_WRITE')")
    @Operation(summary = "Create an order for myself",
            description = "Creates an order for the currently authenticated customer. Requires `ORDER_WRITE`.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing ORDER_WRITE permission")
    })
    public ResponseEntity<OrdersResponse> createMyOrder(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody OrdersDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ordersCommandService.createOrderForCurrentUser(principal.getUsername(), dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ') or @securityService.isOrderOwner(#id)")
    @Operation(summary = "Get order by ID (ADMIN or owner)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Not admin and not the order's owner"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrdersResponse> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(ordersQuerryService.findOrderById(id));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('USER_READ') or @securityService.isCustomerOwner(#customerId)")
    @Operation(summary = "Get orders by customer ID (ADMIN or owner)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Not admin and not the customer")
    })
    public ResponseEntity<OrdersListResponse> getOrdersByCustomer(
            @PathVariable UUID customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ordersQuerryService.findOrdersByCustomerId(customerId, page, size));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_WRITE')")
    @Operation(summary = "Create an order on behalf of a customer (ADMIN)",
            description = "Admin-only. Customers should use POST /api/orders/me.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing USER_WRITE permission"),
            @ApiResponse(responseCode = "404", description = "Customer or product not found")
    })
    public ResponseEntity<OrdersResponse> createOrder(
            @Parameter(description = "Customer ID", required = true) @RequestParam UUID customerId,
            @Valid @RequestBody OrdersDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordersCommandService.createOrder(customerId, dto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    @Operation(summary = "Update order status (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing USER_WRITE permission"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrdersResponse> updateOrderStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ordersCommandService.updateOrderStatus(id, body.get("status")));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    @Operation(summary = "Delete an order (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order deleted"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing USER_WRITE permission"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        ordersCommandService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
