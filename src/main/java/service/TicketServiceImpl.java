package service;

import api.model.TicketCreateRequest;
import api.model.TicketResponse;
import dao.TicketDAO;
import entity.Ticket;
import mapper.TicketApiMapper;
import org.springframework.stereotype.Service;

@Service
public class TicketServiceImpl implements TicketService{

    private final TicketDAO ticketDAO;
    private final TicketApiMapper mapper;

    public TicketServiceImpl(TicketDAO ticketDAO, TicketApiMapper mapper){
        this.ticketDAO = ticketDAO;
        this.mapper = mapper;
    }

    @Override
    public TicketResponse createTicketFromRequest(TicketCreateRequest ticketRequest) {
        if (ticketRequest.getTitle()==null || ticketRequest.getDescription()==null){
            throw new IllegalArgumentException();
        }

        Ticket ticket = Ticket.builder()
                .title(ticketRequest.getTitle())
                .description(ticketRequest.getDescription())
                .build();


        ticketDAO.saveTicket(ticket);
        return null;
    }

    @Override
    public void closeTicket() {

    }

    @Override
    public void updateTicketStatus() {

    }
}
