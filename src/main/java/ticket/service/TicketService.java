package ticket.service;

import api.model.TicketCreateRequest;
import api.model.TicketResponse;
import ticket.entity.Ticket;
import ticket.model.TicketStatus;

public interface TicketService {

    TicketResponse createTicketFromRequest(TicketCreateRequest ticketRequest);

    TicketResponse updateTicketStatus(Ticket ticket, TicketStatus status);

    TicketResponse getTicketDetails(String id);
}
