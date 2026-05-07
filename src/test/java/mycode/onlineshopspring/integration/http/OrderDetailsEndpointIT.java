package mycode.onlineshopspring.integration.http;

import mycode.onlineshopspring.orderDetails.dto.OrderDetailsDto;
import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderDetailsEndpointIT extends AbstractEndpointIntegrationTest {

    @Test
    void getAllOrderDetailsAsAdminReturnsList() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        var customer = persistCustomer();
        var product = persistProduct();
        var order = persistOrder(customer);
        persistOrderDetail(order, product);

        // ACT
        var result = mockMvc.perform(get("/api/order-details")
                .header("Authorization", bearerTokenFor(admin.getUser())));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.orderDetailsList").isArray());
    }

    @Test
    void getOrderDetailByIdAsAdminReturnsDetail() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        var customer = persistCustomer();
        var product = persistProduct();
        var order = persistOrder(customer);
        var detail = persistOrderDetail(order, product);

        // ACT
        var result = mockMvc.perform(get("/api/order-details/{id}", detail.getId())
                .header("Authorization", bearerTokenFor(admin.getUser())));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(detail.getId().toString()))
                .andExpect(jsonPath("$.sku").value(product.getSku()));
    }

    @Test
    void createOrderDetailAsAdminReturns201() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        var product = persistProduct();
        OrderDetailsDto request = new OrderDetailsDto(50, product.getSku(), 2, product.getId());

        // ACT
        var result = mockMvc.perform(post("/api/order-details")
                .header("Authorization", bearerTokenFor(admin.getUser()))
                .contentType(APPLICATION_JSON)
                .content(json(request)));

        // ASSERT
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value(product.getSku()))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    void deleteOrderDetailAsAdminReturns204() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        var customer = persistCustomer();
        var product = persistProduct();
        var order = persistOrder(customer);
        var detail = persistOrderDetail(order, product);

        // ACT
        var result = mockMvc.perform(delete("/api/order-details/{id}", detail.getId())
                .header("Authorization", bearerTokenFor(admin.getUser())));

        // ASSERT
        result.andExpect(status().isNoContent());
    }

    @Test
    void customerCannotListOrderDetails() throws Exception {
        // ARRANGE
        var customer = persistCustomer();

        // ACT
        var result = mockMvc.perform(get("/api/order-details")
                .header("Authorization", bearerTokenFor(customer.getUser())));

        // ASSERT
        result.andExpect(status().isForbidden());
    }
}
