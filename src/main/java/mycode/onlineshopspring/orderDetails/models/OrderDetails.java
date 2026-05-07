package mycode.onlineshopspring.orderDetails.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mycode.onlineshopspring.common.model.AuditableEntity;
import mycode.onlineshopspring.orders.models.Orders;
import mycode.onlineshopspring.products.models.Products;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "order_details")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"order", "product"})
public class OrderDetails extends AuditableEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false, length = 255)
    private String sku;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Products product;


    @Override
    public String toString() {
        return "\n  ID: " + id +
                "\n  price " + price +
                "\n  sku: " + sku +
                "\n  quantity: " + quantity +
                "\n}";
    }

}
