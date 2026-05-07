package mycode.onlineshopspring.auth.user.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignPermissionRequest(@NotBlank String name) {
}
