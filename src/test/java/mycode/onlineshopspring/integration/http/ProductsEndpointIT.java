package mycode.onlineshopspring.integration.http;

import mycode.onlineshopspring.products.dto.ProductsDto;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductsEndpointIT extends AbstractEndpointIntegrationTest {

    @Test
    void createProductCreatesRecord() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        ProductsDto request = new ProductsDto(
                "SKU-NEW", "Laptop", 5000, 3, "Gaming laptop", "electronics", new Date(), 5);

        // ACT
        var result = mockMvc.perform(post("/api/products")
                .header("Authorization", bearerTokenFor(admin.getUser()))
                .contentType(APPLICATION_JSON)
                .content(json(request)));

        // ASSERT
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("SKU-NEW"))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(5000));
    }

    @Test
    void getAllProductsReturnsList() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        persistProduct();

        // ACT
        var result = mockMvc.perform(get("/api/products")
                .header("Authorization", bearerTokenFor(admin.getUser())));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.productsList").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber());
    }

    @Test
    void getProductByIdReturnsProduct() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        var product = persistProduct();

        // ACT
        var result = mockMvc.perform(get("/api/products/{id}", product.getId())
                .header("Authorization", bearerTokenFor(admin.getUser())));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId().toString()))
                .andExpect(jsonPath("$.sku").value(product.getSku()));
    }

    @Test
    void updateProductChangesValues() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        var product = persistProduct();
        ProductsDto request = new ProductsDto(
                "SKU-UPD", "Updated Name", 999, 2, "updated", "books", new Date(), 50);

        // ACT
        var result = mockMvc.perform(put("/api/products/{id}", product.getId())
                .header("Authorization", bearerTokenFor(admin.getUser()))
                .contentType(APPLICATION_JSON)
                .content(json(request)));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.price").value(999));
    }

    @Test
    void deleteProductRemovesRecord() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        var product = persistProduct();

        // ACT
        var result = mockMvc.perform(delete("/api/products/{id}", product.getId())
                .header("Authorization", bearerTokenFor(admin.getUser())));

        // ASSERT
        result.andExpect(status().isNoContent());
    }

    @Test
    void customerCanReadProducts() throws Exception {
        // ARRANGE
        var customer = persistCustomer();

        // ACT
        var result = mockMvc.perform(get("/api/products")
                .header("Authorization", bearerTokenFor(customer.getUser())));

        // ASSERT
        result.andExpect(status().isOk());
    }

    @Test
    void customerCannotCreateProduct() throws Exception {
        // ARRANGE
        var customer = persistCustomer();
        ProductsDto request = new ProductsDto(
                "SKU-X", "x", 1, 1, "x", "x", new Date(), 1);

        // ACT
        var result = mockMvc.perform(post("/api/products")
                .header("Authorization", bearerTokenFor(customer.getUser()))
                .contentType(APPLICATION_JSON)
                .content(json(request)));

        // ASSERT
        result.andExpect(status().isForbidden());
    }

    @Test
    void anonymousCannotAccessProducts() throws Exception {
        // ACT
        var result = mockMvc.perform(get("/api/products"));

        // ASSERT
        result.andExpect(status().isUnauthorized());
    }
}
