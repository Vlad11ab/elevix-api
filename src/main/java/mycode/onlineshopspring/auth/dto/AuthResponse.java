package mycode.onlineshopspring.auth.dto;

import java.util.List;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String email,
        List<String> permissions
) {
}
