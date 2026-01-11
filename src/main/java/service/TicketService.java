package service;

import api.model.TicketCreateRequest;
import api.model.TicketResponse;

public interface TicketService {

    TicketResponse createTicketFromRequest(TicketCreateRequest ticketRequest);

    void closeTicket();

    void updateTicketStatus();
}
