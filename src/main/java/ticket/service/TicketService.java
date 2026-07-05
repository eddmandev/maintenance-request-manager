package ticket.service;

import api.model.*;
import ticket.exception.SameTicketPriorityException;
import ticket.exception.SameTicketStatusException;
import ticket.exception.TicketNotFoundException;

public interface TicketService {

    TicketResponse createTicketFromRequest(TicketCreateRequest ticketRequest);

    TicketResponse updateTicketStatus(TicketStatusUpdateRequest ticketUpdateStatusRequest, long id) throws SameTicketStatusException;

    TicketResponse getTicketDetails(long id) throws TicketNotFoundException;

    TicketResponse updateTicketPriority(TicketUpdatePriorityRequest ticketUpdatePriorityRequest, long id)
            throws TicketNotFoundException, SameTicketPriorityException;
}
