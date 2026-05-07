package mycode.onlineshopspring.orders.repository;

import mycode.onlineshopspring.orders.models.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrdersRepository extends JpaRepository<Orders, UUID> {

    @Override
    @EntityGraph(attributePaths = {"orderDetailsSet", "orderDetailsSet.product"})
    List<Orders> findAll();

    @EntityGraph(attributePaths = {"orderDetailsSet", "orderDetailsSet.product"})
    Page<Orders> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"orderDetailsSet", "orderDetailsSet.product"})
    Page<Orders> findByCustomerId(UUID customerId, Pageable pageable);

    @EntityGraph(attributePaths = {"orderDetailsSet", "orderDetailsSet.product"})
    Optional<Orders> findById(UUID id);
}
