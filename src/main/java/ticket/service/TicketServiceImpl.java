package ticket.service;

import api.model.TicketCreateRequest;
import api.model.TicketResponse;
import api.model.TicketStatus;
import api.model.TicketStatusUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticket.entity.Ticket;
import ticket.exception.TicketNotFoundException;
import ticket.exception.UnchangedTicketStatusException;
import ticket.mapper.TicketApiMapper;
import ticket.repository.TicketRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketApiMapper mapper;
    private final TicketRepository ticketRepository;


    @Override
    public TicketResponse createTicketFromRequest(TicketCreateRequest ticketRequest) {
        var ticketEntity = mapper.mapToEntity(ticketRequest);
        ticketRepository.save(ticketEntity);
        return mapper.mapToResponse(ticketEntity);
    }

    @Override
    public TicketResponse updateTicketStatus(TicketStatusUpdateRequest ticketUpdateRequest, long id) throws TicketNotFoundException, UnchangedTicketStatusException {
        var ticket = ticketRepository.findById(id);
        if (Objects.isNull(ticket)){
            throw new TicketNotFoundException("Couldn't find ticket of id: " + id);
        }
        if (isSameStatus(ticket, ticketUpdateRequest.getStatus())){
            throw new UnchangedTicketStatusException("The status of the ticket is the same.");
        }

        var ticketStatus = mapper.mapTicketStatusToEntity(ticketUpdateRequest.getStatus());
        ticket.setStatus(ticketStatus);
        ticketRepository.save(ticket);
        return mapper.mapToResponse(ticket);
    }

    @Override
    public TicketResponse getTicketDetails(String id) {
        var ticket = ticketRepository.findById(Long.parseLong(id));
        return mapper.mapToResponse(ticket);
    }

    private boolean isSameStatus(Ticket ticket, TicketStatus ticketStatus){
        return ticket.getStatus().equals(mapper.mapTicketStatusToEntity(ticketStatus));
    }
}
