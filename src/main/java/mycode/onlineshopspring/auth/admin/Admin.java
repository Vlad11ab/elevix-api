package mycode.onlineshopspring.auth.admin;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mycode.onlineshopspring.auth.user.User;
import mycode.onlineshopspring.common.model.AuditableEntity;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "admin")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true, exclude = "user")
public class Admin extends AuditableEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, length = 255)
    private String displayName;

    @Column(length = 255)
    private String department;

    @Column(length = 1000)
    private String notes;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
