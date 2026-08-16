package wit.edu.inz.user.entity;

import lombok.Builder;
import wit.edu.inz.role.entity.Role;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name ="users")
@Data
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private boolean active;

    @Column(name = "address_line")
    private String addressLine;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "building_nr")
    private int buildingNr;

    @Column(name = "apartment_nr")
    private int apartmentNr;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean enabled;
}