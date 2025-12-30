package service;

import dao.TicketDAO;
import entity.Ticket;
import org.springframework.stereotype.Service;

@Service
public class TicketServiceImpl implements TicketService{

    private final TicketDAO ticketDAO;

    public TicketServiceImpl(TicketDAO ticketDAO){
        this.ticketDAO = ticketDAO;
    }

    @Override
    public Ticket createTicketFromRequest() {
        return null;
    }

    @Override
    public void closeTicket() {

    }

    @Override
    public void updateTicketStatus() {

    }
}
