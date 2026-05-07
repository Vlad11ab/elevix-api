package mycode.onlineshopspring.orderDetails.repository;

import mycode.onlineshopspring.orderDetails.models.OrderDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, UUID> {

    @Override
    @EntityGraph(attributePaths = "product")
    List<OrderDetails> findAll();

    @EntityGraph(attributePaths = "product")
    Page<OrderDetails> findAll(Pageable pageable);
}
