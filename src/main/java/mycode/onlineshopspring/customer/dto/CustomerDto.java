package mycode.onlineshopspring.customer.dto;

public record CustomerDto(
        String fullName,
        String billingAddress,
        String defaultShippingAddress,
        String country,
        String phone
) {
}
