package mycode.onlineshopspring.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank String fullName,
        @NotBlank String billingAddress,
        @NotBlank String defaultShippingAddress,
        @NotBlank String country,
        @NotBlank String phone
) {
}
