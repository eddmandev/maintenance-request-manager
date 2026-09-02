package wit.edu.inz.ticket.service;

import api.model.*;
import wit.edu.inz.ticket.exception.SameTicketPriorityException;
import wit.edu.inz.ticket.exception.SameTicketStatusException;
import wit.edu.inz.ticket.exception.TicketNotFoundException;

public interface TicketService {

    TicketResponse createTicketFromRequest(TicketCreateRequest ticketRequest, String authenticationName);

    TicketResponse updateTicketStatus(TicketStatusUpdateRequest ticketUpdateStatusRequest, long id) throws SameTicketStatusException;

    TicketResponse getTicketDetails(long id) throws TicketNotFoundException;

    TicketResponse updateTicketPriority(TicketUpdatePriorityRequest ticketUpdatePriorityRequest, long id)
            throws TicketNotFoundException, SameTicketPriorityException;

    TicketResponse createFollowUpTicket(Long ticketId, TicketCreateRequest request, String username);

    TicketResponse assignWorker(Long ticketId, String username);

    TicketResponse editTicket(Long ticketId, TicketUpdateRequest updateRequest, String username);
}
