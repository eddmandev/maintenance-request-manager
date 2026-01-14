package service;

import api.model.TicketCreateRequest;
import api.model.TicketResponse;
import model.TicketStatus;

public interface TicketService {

    TicketResponse createTicketFromRequest(TicketCreateRequest ticketRequest);

    void updateTicketStatus(TicketStatus status);

    TicketResponse getTicketDetails(String id);
}
