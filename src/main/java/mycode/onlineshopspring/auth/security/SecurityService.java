package mycode.onlineshopspring.auth.security;

import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.auth.customer.Customer;
import mycode.onlineshopspring.auth.customer.CustomerRepository;
import mycode.onlineshopspring.orders.models.Orders;
import mycode.onlineshopspring.orders.repository.OrdersRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final CustomerRepository customerRepository;
    private final OrdersRepository ordersRepository;

    @Transactional(readOnly = true)
    public boolean isCustomerOwner(UUID customerId) {
        String email = currentEmail();
        if (email == null) return false;
        return customerRepository.findById(customerId)
                .map(Customer::getUser)
                .map(user -> email.equals(user.getEmail()))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean isOrderOwner(UUID orderId) {
        String email = currentEmail();
        if (email == null) return false;
        return ordersRepository.findById(orderId)
                .map(Orders::getCustomer)
                .map(Customer::getUser)
                .map(user -> email.equals(user.getEmail()))
                .orElse(false);
    }

    private String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails ud) return ud.getUsername();
        return null;
    }
}
