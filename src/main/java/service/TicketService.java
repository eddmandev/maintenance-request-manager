package service;

import api.model.TicketCreateRequest;
import api.model.TicketResponse;
import entity.Ticket;

public interface TicketService {

    TicketResponse createTicketFromRequest(TicketCreateRequest ticketRequest);

    void closeTicket();

    void updateTicketStatus();
}
