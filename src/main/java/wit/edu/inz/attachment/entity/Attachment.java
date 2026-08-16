package wit.edu.inz.attachment.entity;

import jakarta.persistence.*;
import lombok.*;
import wit.edu.inz.ticket.entity.Ticket;
import wit.edu.inz.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "attachments",
        indexes = {
                @Index(name = "idx_attachment_ticket", columnList = "ticket_id"),
                @Index(name = "idx_attachment_uploaded_by", columnList = "uploaded_by")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "stored_filename", nullable = false, unique = true)
    private String storedFilename;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @PrePersist
    public void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}