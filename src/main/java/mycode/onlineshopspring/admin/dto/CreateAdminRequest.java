package mycode.onlineshopspring.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAdminRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank String displayName,
        String department,
        String notes
) {
}
