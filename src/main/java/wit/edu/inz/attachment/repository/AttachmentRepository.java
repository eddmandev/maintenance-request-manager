package wit.edu.inz.attachment.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import wit.edu.inz.attachment.entity.Attachment;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByTicketId(Long ticketId);
}