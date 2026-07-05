package ticket.repository;

import ticket.entity.Ticket;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TicketRepository extends CrudRepository<Ticket, Long> {
    Optional<Ticket> findById(long id);

    Ticket save();
}
