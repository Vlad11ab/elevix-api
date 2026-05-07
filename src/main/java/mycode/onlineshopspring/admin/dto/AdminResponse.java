package mycode.onlineshopspring.admin.dto;

import java.util.UUID;

public record AdminResponse(
        UUID id,
        String email,
        String displayName,
        String department,
        String notes
) {
}
