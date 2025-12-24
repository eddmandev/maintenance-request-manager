package service;

import dao.TicketDAO;
import entity.Ticket;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TicketService {

    private final TicketDAO ticketDAO;

    public Ticket createNewTicket(){
        return null;
    }

    public Ticket updateTicket(){
        return null;
    }
}
