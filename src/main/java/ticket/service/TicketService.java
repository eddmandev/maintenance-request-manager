package ticket.service;

import api.model.TicketCreateRequest;
import api.model.TicketResponse;
import api.model.TicketStatusUpdateRequest;

public interface TicketService {

    TicketResponse createTicketFromRequest(TicketCreateRequest ticketRequest);

    TicketResponse updateTicketStatus(TicketStatusUpdateRequest ticketUpdateStatusRequest, long id) throws Exception;

    TicketResponse getTicketDetails(String id);
}
