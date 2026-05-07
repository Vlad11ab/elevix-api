package mycode.onlineshopspring.products.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mycode.onlineshopspring.common.model.AuditableEntity;
import mycode.onlineshopspring.orderDetails.models.OrderDetails;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true, exclude = "orderDetailsSet")
public class Products extends AuditableEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, length = 255)
    private String sku;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int weight;

    @Column(nullable = false, length = 255)
    private String descriptions;

    @Column(nullable = false, length = 255)
    private String category;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private int stock;

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<OrderDetails> orderDetailsSet = new HashSet<>();

}
