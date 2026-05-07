package mycode.onlineshopspring.products.repository;

import mycode.onlineshopspring.products.models.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductsRepository extends JpaRepository<Products, UUID> {

}
