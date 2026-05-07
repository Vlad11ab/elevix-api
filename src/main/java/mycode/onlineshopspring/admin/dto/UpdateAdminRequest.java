package mycode.onlineshopspring.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAdminRequest(
        @NotBlank String displayName,
        String department,
        String notes
) {
}
