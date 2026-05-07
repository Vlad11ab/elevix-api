package mycode.onlineshopspring.auth.admin;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, UUID> {

    @Override
    @EntityGraph(attributePaths = "user")
    List<Admin> findAll();

    @EntityGraph(attributePaths = "user")
    Optional<Admin> findByUserEmail(String email);
}
