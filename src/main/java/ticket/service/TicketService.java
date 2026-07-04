package ticket.service;

import api.model.TicketCreateRequest;
import api.model.TicketResponse;
import entity.Ticket;
import model.TicketStatus;
import ticket.model.TicketStatus;

public interface TicketService {

    TicketResponse createTicketFromRequest(TicketCreateRequest ticketRequest);

    TicketResponse updateTicketStatus(Ticket ticket, TicketStatus status);

    TicketResponse getTicketDetails(String id);
}
