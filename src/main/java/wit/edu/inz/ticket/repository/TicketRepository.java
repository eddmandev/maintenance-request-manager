package wit.edu.inz.ticket.repository;

import wit.edu.inz.ticket.entity.Ticket;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TicketRepository extends CrudRepository<Ticket, Long> {
    Optional<Ticket> findById(long id);
}
