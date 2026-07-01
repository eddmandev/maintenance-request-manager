package role.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import role.model.RoleName;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleName roleName;
}