package mycode.onlineshopspring.integration.http;

import mycode.onlineshopspring.admin.dto.CreateAdminRequest;
import mycode.onlineshopspring.admin.dto.UpdateAdminRequest;
import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminsEndpointIT extends AbstractEndpointIntegrationTest {

    @Test
    void getAllAdminsAsAdminReturnsList() throws Exception {
        // ARRANGE
        var admin = persistAdmin();

        // ACT
        var result = mockMvc.perform(get("/api/admins")
                .header("Authorization", bearerTokenFor(admin.getUser())));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getMyAdminProfileReturnsOwnData() throws Exception {
        // ARRANGE
        var admin = persistAdmin();

        // ACT
        var result = mockMvc.perform(get("/api/admins/me")
                .header("Authorization", bearerTokenFor(admin.getUser())));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(admin.getUser().getEmail()))
                .andExpect(jsonPath("$.displayName").value(admin.getDisplayName()));
    }

    @Test
    void updateMyAdminProfileChangesValues() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        UpdateAdminRequest request = new UpdateAdminRequest("Updated Name", "New Dept", "New notes");

        // ACT
        var result = mockMvc.perform(put("/api/admins/me")
                .header("Authorization", bearerTokenFor(admin.getUser()))
                .contentType(APPLICATION_JSON)
                .content(json(request)));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Updated Name"))
                .andExpect(jsonPath("$.department").value("New Dept"));
    }

    @Test
    void getAdminByIdReturnsAdmin() throws Exception {
        // ARRANGE
        var admin = persistAdmin();

        // ACT
        var result = mockMvc.perform(get("/api/admins/{id}", admin.getId())
                .header("Authorization", bearerTokenFor(admin.getUser())));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(admin.getId().toString()));
    }

    @Test
    void createAdminAsAdminReturns201() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        CreateAdminRequest request = new CreateAdminRequest(
                "new-admin@shop.test", "password123", "New Admin", "Sales", "Test note");

        // ACT
        var result = mockMvc.perform(post("/api/admins")
                .header("Authorization", bearerTokenFor(admin.getUser()))
                .contentType(APPLICATION_JSON)
                .content(json(request)));

        // ASSERT
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new-admin@shop.test"))
                .andExpect(jsonPath("$.displayName").value("New Admin"));
    }

    @Test
    void updateAdminByIdReturnsUpdatedAdmin() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        var target = persistAdmin();
        UpdateAdminRequest request = new UpdateAdminRequest("Edited By Admin", "HR", "Edited");

        // ACT
        var result = mockMvc.perform(put("/api/admins/{id}", target.getId())
                .header("Authorization", bearerTokenFor(admin.getUser()))
                .contentType(APPLICATION_JSON)
                .content(json(request)));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Edited By Admin"))
                .andExpect(jsonPath("$.department").value("HR"));
    }

    @Test
    void deleteAdminAsAdminReturns204() throws Exception {
        // ARRANGE
        var admin = persistAdmin();
        var target = persistAdmin();

        // ACT
        var result = mockMvc.perform(delete("/api/admins/{id}", target.getId())
                .header("Authorization", bearerTokenFor(admin.getUser())));

        // ASSERT
        result.andExpect(status().isNoContent());
    }

    @Test
    void customerCannotListAdmins() throws Exception {
        // ARRANGE
        var customer = persistCustomer();

        // ACT
        var result = mockMvc.perform(get("/api/admins")
                .header("Authorization", bearerTokenFor(customer.getUser())));

        // ASSERT
        result.andExpect(status().isForbidden());
    }
}
