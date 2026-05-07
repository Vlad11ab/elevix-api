package mycode.onlineshopspring.auth.permission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreatePermissionRequest(
        @NotBlank
        @Pattern(regexp = "^[A-Z][A-Z0-9_]{2,63}$",
                message = "Permission name must be UPPER_SNAKE_CASE, 3-64 chars, starting with a letter (e.g. CUSTOM_READ).")
        String name
) {
}
