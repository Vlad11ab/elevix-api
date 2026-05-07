package mycode.onlineshopspring.auth.customer;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mycode.onlineshopspring.auth.user.User;
import mycode.onlineshopspring.common.model.AuditableEntity;
import mycode.onlineshopspring.orders.models.Orders;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "customer")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"orderSet", "user"})
public class Customer extends AuditableEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, length = 255)
    private String fullName;

    @Column(nullable = false, length = 255)
    private String billingAddress;

    @Column(nullable = false, length = 255)
    private String defaultShippingAddress;

    @Column(nullable = false, length = 255)
    private String country;

    @Column(nullable = false, length = 255)
    private String phone;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Orders> orderSet = new HashSet<>();

    @Override
    public String toString() {
        return "Customer Details {" +
                "\n  ID: " + id +
                "\n  Full Name: " + fullName +
                "\n  Email: " + (user != null ? user.getEmail() : "(no user)") +
                "\n  Phone: " + phone +
                "\n  Country: " + country +
                "\n  Billing Address: " + billingAddress +
                "\n  Default Shipping Address: " + defaultShippingAddress +
                "\n}";
    }

    public void addOrder(Orders order) {
        orderSet.add(order);
        order.setCustomer(this);
    }
}
