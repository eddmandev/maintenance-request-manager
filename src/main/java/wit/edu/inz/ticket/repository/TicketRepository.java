package wit.edu.inz.ticket.repository;

import wit.edu.inz.ticket.entity.Ticket;
import org.springframework.data.repository.CrudRepository;
import wit.edu.inz.ticket.entity.TicketStatus;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends CrudRepository<Ticket, Long> {
    Optional<Ticket> findById(long id);

    List<Ticket> findAllByCreatedByUsername(String username);

    List<Ticket> findAllByAssignedWorkerIsNullAndStatus(TicketStatus ticketStatus);
}
