package ticket.service;

import api.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticket.entity.Ticket;
import ticket.exception.SameTicketPriorityException;
import ticket.exception.TicketClosedException;
import ticket.exception.TicketNotFoundException;
import ticket.exception.SameTicketStatusException;
import ticket.mapper.TicketApiMapper;
import ticket.repository.TicketRepository;

import java.util.Objects;


@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketApiMapper mapper;

    @Override
    public TicketResponse createTicketFromRequest(TicketCreateRequest ticketRequest) {
        Ticket ticket = mapper.mapToEntity(ticketRequest);
        ticket = ticketRepository.save(ticket);
        return mapper.mapToResponse(ticket);
    }

    @Override
    public TicketResponse updateTicketStatus(TicketStatusUpdateRequest ticketUpdateRequest, long id)
            throws TicketNotFoundException, SameTicketStatusException {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new TicketNotFoundException("Couldn't find ticket with id: " + id));

        var ticketStatusRequest = ticketUpdateRequest.getStatus();

        if (isSameStatus(ticket, ticketUpdateRequest.getStatus())) {
            throw new SameTicketStatusException("The status of the ticket is already " + ticketUpdateRequest.getStatus());
        }

        if (ticketStatusRequest.equals(TicketStatus.COMPLETED) && !isTicketAssigned(ticket)){
            throw new TicketClosedException("The ticket must be assigned for it to be completed.");
        }

        ticket.setStatus(mapper.mapTicketStatusToEntity(ticketStatusRequest));
        ticket = ticketRepository.save(ticket);
        return mapper.mapToResponse(ticket);
    }

    @Override
    public TicketResponse updateTicketPriority(TicketUpdatePriorityRequest ticketUpdatePriorityRequest, long id)
            throws TicketNotFoundException, SameTicketPriorityException {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new TicketNotFoundException("Couldn't find ticket with id: " + id));

        if (isSamePriority(ticket, ticketUpdatePriorityRequest.getPriority())) {
            throw new SameTicketPriorityException("The priority of the ticket is already " + ticketUpdatePriorityRequest.getPriority());
        }
        ticket.setPriority(mapper.mapTicketPriorityToEntity(ticketUpdatePriorityRequest.getPriority()));
        ticket = ticketRepository.save(ticket);

        return mapper.mapToResponse(ticket);
    }

    @Override
    public TicketResponse getTicketDetails(long id) throws TicketNotFoundException {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new TicketNotFoundException("Couldn't find ticket with id: " + id));
        return mapper.mapToResponse(ticket);
    }

    private boolean isSameStatus(Ticket ticket, api.model.TicketStatus status) {
        return ticket.getStatus().equals(mapper.mapTicketStatusToEntity(status));
    }

    private boolean isSamePriority(Ticket ticket, api.model.TicketPriority priority) {
        return ticket.getPriority().equals(mapper.mapTicketPriorityToEntity(priority));
    }

    private boolean isTicketAssigned(Ticket ticket){
        return !Objects.isNull(ticket.getAssignedWorker());
    }
}