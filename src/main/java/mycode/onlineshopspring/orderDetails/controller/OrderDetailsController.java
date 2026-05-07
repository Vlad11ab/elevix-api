package mycode.onlineshopspring.orderDetails.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.orderDetails.dto.OrderDetailsDto;
import mycode.onlineshopspring.orderDetails.dto.OrderDetailsListResponse;
import mycode.onlineshopspring.orderDetails.dto.OrderDetailsResponse;
import mycode.onlineshopspring.orderDetails.service.OrderDetailsCommandService;
import mycode.onlineshopspring.orderDetails.service.OrderDetailsQuerryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/order-details")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Order Details", description = "Line items within orders. Listing all is admin-only.")
public class OrderDetailsController {

    private final OrderDetailsQuerryService orderDetailsQuerryService;
    private final OrderDetailsCommandService orderDetailsCommandService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    @Operation(summary = "List all order details (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing USER_READ permission")
    })
    public ResponseEntity<OrderDetailsListResponse> getAllOrderDetails(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderDetailsQuerryService.findAllOrderDetails(page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    @Operation(summary = "Get order detail by ID (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order detail found"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing USER_READ permission"),
            @ApiResponse(responseCode = "404", description = "Order detail not found")
    })
    public ResponseEntity<OrderDetailsResponse> getOrderDetailById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderDetailsQuerryService.findOrderDetailById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_WRITE')")
    @Operation(summary = "Create order detail (ADMIN)",
            description = "Admin-only line item creation outside an order. Customers should include order details in POST /api/orders/me.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order detail created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing USER_WRITE permission"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<OrderDetailsResponse> createOrderDetails(@Valid @RequestBody OrderDetailsDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDetailsCommandService.createOrderDetails(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    @Operation(summary = "Delete order detail (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order detail deleted"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing USER_WRITE permission"),
            @ApiResponse(responseCode = "404", description = "Order detail not found")
    })
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable UUID id) {
        orderDetailsCommandService.deleteOrderDetail(id);
        return ResponseEntity.noContent().build();
    }
}
