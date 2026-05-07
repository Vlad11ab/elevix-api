package mycode.onlineshopspring.integration.http;

import mycode.onlineshopspring.customer.dto.CustomerDto;
import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomersEndpointIT extends AbstractEndpointIntegrationTest {

    @Test
    void getAllCustomersAsAdminReturnsList() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        persistCustomer();

        // ACT
        var result = mockMvc.perform(get("/api/customers")
                .header("Authorization", bearerTokenFor(admin.getUser())));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.customers").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber());
    }

    @Test
    void getMyProfileAsCustomerReturnsOwnData() throws Exception {
        // ARRANGE
        var customer = persistCustomer();

        // ACT
        var result = mockMvc.perform(get("/api/customers/me")
                .header("Authorization", bearerTokenFor(customer.getUser())));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(customer.getUser().getEmail()))
                .andExpect(jsonPath("$.fullName").value(customer.getFullName()));
    }

    @Test
    void updateMyProfileAsCustomerChangesValues() throws Exception {
        // ARRANGE
        var customer = persistCustomer();
        CustomerDto request = new CustomerDto(
                "New Name", "New Billing", "New Shipping", "FR", "0700111222");

        // ACT
        var result = mockMvc.perform(put("/api/customers/me")
                .header("Authorization", bearerTokenFor(customer.getUser()))
                .contentType(APPLICATION_JSON)
                .content(json(request)));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("New Name"))
                .andExpect(jsonPath("$.country").value("FR"));
    }

    @Test
    void getCustomerByIdAsAdminReturnsCustomer() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        var customer = persistCustomer();

        // ACT
        var result = mockMvc.perform(get("/api/customers/{id}", customer.getId())
                .header("Authorization", bearerTokenFor(admin.getUser())));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customer.getId().toString()));
    }

    @Test
    void updateCustomerAsAdminChangesValues() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        var customer = persistCustomer();
        CustomerDto request = new CustomerDto(
                "Admin Edit", "Admin Billing", "Admin Shipping", "DE", "0700333444");

        // ACT
        var result = mockMvc.perform(put("/api/customers/{id}", customer.getId())
                .header("Authorization", bearerTokenFor(admin.getUser()))
                .contentType(APPLICATION_JSON)
                .content(json(request)));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Admin Edit"))
                .andExpect(jsonPath("$.country").value("DE"));
    }

    @Test
    void deleteCustomerAsAdminReturns204() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        var customer = persistCustomer();

        // ACT
        var result = mockMvc.perform(delete("/api/customers/{id}", customer.getId())
                .header("Authorization", bearerTokenFor(admin.getUser())));

        // ASSERT
        result.andExpect(status().isNoContent());
    }

    @Test
    void customerCannotListAllCustomers() throws Exception {
        // ARRANGE
        var customer = persistCustomer();

        // ACT
        var result = mockMvc.perform(get("/api/customers")
                .header("Authorization", bearerTokenFor(customer.getUser())));

        // ASSERT
        result.andExpect(status().isForbidden());
    }
}
