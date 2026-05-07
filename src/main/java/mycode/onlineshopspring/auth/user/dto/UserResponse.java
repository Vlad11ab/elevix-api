package mycode.onlineshopspring.auth.user.dto;

import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        boolean enabled,
        List<String> permissions
) {
}
