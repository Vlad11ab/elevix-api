package mycode.onlineshopspring.integration.http;

import mycode.onlineshopspring.orders.dto.OrdersDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrdersEndpointIT extends AbstractEndpointIntegrationTest {

    @Test
    void getAllOrdersAsAdminReturnsList() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        var customer = persistCustomer();
        persistOrder(customer);

        // ACT
        var result = mockMvc.perform(get("/api/orders")
                .header("Authorization", bearerTokenFor(admin.getUser())));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.ordersList").isArray());
    }

    @Test
    void getMyOrdersAsCustomerReturnsOwnOrders() throws Exception {
        // ARRANGE
        var customer = persistCustomer();
        persistOrder(customer);

        // ACT
        var result = mockMvc.perform(get("/api/orders/me")
                .header("Authorization", bearerTokenFor(customer.getUser())));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.ordersList").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void createMyOrderAsCustomerReturns201() throws Exception {
        // ARRANGE
        var customer = persistCustomer();
        OrdersDto request = new OrdersDto(
                250, "Shipping 1", "Address 1", customer.getUser().getEmail(),
                LocalDate.now(), "PENDING", new HashSet<>());

        // ACT
        var result = mockMvc.perform(post("/api/orders/me")
                .header("Authorization", bearerTokenFor(customer.getUser()))
                .contentType(APPLICATION_JSON)
                .content(json(request)));

        // ASSERT
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.ammount").value(250))
                .andExpect(jsonPath("$.orderStatus").value("PENDING"));
    }

    @Test
    void getOrderByIdAsAdminReturnsOrder() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        var customer = persistCustomer();
        var order = persistOrder(customer);

        // ACT
        var result = mockMvc.perform(get("/api/orders/{id}", order.getId())
                .header("Authorization", bearerTokenFor(admin.getUser())));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().toString()));
    }

    @Test
    void getOrdersByCustomerAsAdminReturnsList() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        var customer = persistCustomer();
        persistOrder(customer);

        // ACT
        var result = mockMvc.perform(get("/api/orders/customer/{customerId}", customer.getId())
                .header("Authorization", bearerTokenFor(admin.getUser())));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void createOrderAsAdminForCustomerReturns201() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        var customer = persistCustomer();
        OrdersDto request = new OrdersDto(
                500, "Shipping A", "Address A", customer.getUser().getEmail(),
                LocalDate.now(), "PENDING", new HashSet<>());

        // ACT
        var result = mockMvc.perform(post("/api/orders")
                .header("Authorization", bearerTokenFor(admin.getUser()))
                .param("customerId", customer.getId().toString())
                .contentType(APPLICATION_JSON)
                .content(json(request)));

        // ASSERT
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.ammount").value(500));
    }

    @Test
    void updateOrderStatusAsAdminReturnsUpdatedOrder() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        var customer = persistCustomer();
        var order = persistOrder(customer);

        // ACT
        var result = mockMvc.perform(patch("/api/orders/{id}/status", order.getId())
                .header("Authorization", bearerTokenFor(admin.getUser()))
                .contentType(APPLICATION_JSON)
                .content(json(Map.of("status", "SHIPPED"))));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("SHIPPED"));
    }

    @Test
    void deleteOrderAsAdminReturns204() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        var customer = persistCustomer();
        var order = persistOrder(customer);

        // ACT
        var result = mockMvc.perform(delete("/api/orders/{id}", order.getId())
                .header("Authorization", bearerTokenFor(admin.getUser())));

        // ASSERT
        result.andExpect(status().isNoContent());
    }

    @Test
    void customerCannotListAllOrders() throws Exception {
        // ARRANGE
        var customer = persistCustomer();

        // ACT
        var result = mockMvc.perform(get("/api/orders")
                .header("Authorization", bearerTokenFor(customer.getUser())));

        // ASSERT
        result.andExpect(status().isForbidden());
    }
}
