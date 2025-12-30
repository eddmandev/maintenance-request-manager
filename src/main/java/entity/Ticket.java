package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import model.TicketStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime createdAt;
}
