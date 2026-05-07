package mycode.onlineshopspring.products.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.products.dto.ProductsDto;
import mycode.onlineshopspring.products.dto.ProductsListResponse;
import mycode.onlineshopspring.products.dto.ProductsResponse;
import mycode.onlineshopspring.products.service.ProductsCommandService;
import mycode.onlineshopspring.products.service.ProductsQuerryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Products", description = "Product catalog. Read open to any authenticated user; write requires PRODUCT_WRITE (admin).")
public class ProductsController {

    private final ProductsQuerryService productsQuerryService;
    private final ProductsCommandService productsCommandService;

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "List all products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing PRODUCT_READ permission")
    })
    public ResponseEntity<ProductsListResponse> getAllProducts(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productsQuerryService.findAllProducts(page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get product by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing PRODUCT_READ permission"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductsResponse> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productsQuerryService.findProductById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    @Operation(summary = "Create a new product (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing PRODUCT_WRITE permission")
    })
    public ResponseEntity<ProductsResponse> createProduct(@Valid @RequestBody ProductsDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productsCommandService.createProduct(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    @Operation(summary = "Update a product (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing PRODUCT_WRITE permission"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductsResponse> updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductsDto dto) {
        return ResponseEntity.ok(productsCommandService.updateProduct(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    @Operation(summary = "Delete a product (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product deleted"),
            @ApiResponse(responseCode = "401", description = "Missing token"),
            @ApiResponse(responseCode = "403", description = "Missing PRODUCT_WRITE permission"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productsCommandService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
