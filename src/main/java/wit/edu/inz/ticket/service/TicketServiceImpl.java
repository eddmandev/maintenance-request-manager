package wit.edu.inz.ticket.service;

import api.model.*;
import wit.edu.inz.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wit.edu.inz.role.entity.Role;
import wit.edu.inz.ticket.entity.Ticket;
import wit.edu.inz.ticket.entity.TicketPriority;
import wit.edu.inz.ticket.entity.TicketStatus;
import wit.edu.inz.ticket.exception.SameTicketPriorityException;
import wit.edu.inz.ticket.exception.TicketNotFoundException;
import wit.edu.inz.ticket.exception.SameTicketStatusException;
import wit.edu.inz.ticket.mapper.TicketApiMapper;
import wit.edu.inz.ticket.repository.TicketRepository;
import wit.edu.inz.user.entity.User;
import wit.edu.inz.user.repository.UserRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketApiMapper mapper;
    private final UserRepository userRepository;

    @Override
    public TicketResponse createTicketFromRequest(
            TicketCreateRequest request,
            String username) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found."));

        Ticket ticket = mapper.mapToEntity(request);

        ticket.setCreatedBy(creator);
        ticket.setStatus(wit.edu.inz.ticket.entity.TicketStatus.OPEN);

        ticket.setPriority(calculatePriority(request.getType()));

        return mapper.mapToResponse(ticketRepository.save(ticket));
    }

    @Override
    public TicketResponse createFollowUpTicket(
            Long ticketId,
            TicketCreateRequest request,
            String username) {
        Ticket parentTicket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new TicketNotFoundException(
                                "Ticket with id " + ticketId + " was not found."));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User " + username + " was not found."));

        Ticket followUp = mapper.mapToEntity(request);

        followUp.setCreatedBy(currentUser);
        followUp.setParentTicket(parentTicket);
        followUp.setStatus(TicketStatus.OPEN);

        Ticket savedTicket = ticketRepository.save(followUp);

        return mapper.mapToResponse(savedTicket);
    }

    @Override
    public TicketResponse updateTicketStatus(
            TicketStatusUpdateRequest request,
            long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new TicketNotFoundException(
                                "Ticket with id '" + id + "' doesn't exist."
                        ));

        TicketStatus newStatus =
                mapper.mapTicketStatusToEntity(request.getStatus());

        if (ticket.getStatus() == newStatus) {
            throw new SameTicketStatusException(
                    "Ticket already has status " + newStatus + "."
            );
        }

        ticket.setStatus(newStatus);

        Ticket updated = ticketRepository.save(ticket);

        return mapper.mapToResponse(updated);
    }

    @Override
    public TicketResponse getTicketDetails(long id) throws TicketNotFoundException {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new TicketNotFoundException(
                                "Ticket with id '" + id + "' doesn't exist."
                        ));

        return mapper.mapToResponse(ticket);
    }

    @Override
    public TicketResponse updateTicketPriority(
            TicketUpdatePriorityRequest request,
            long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new TicketNotFoundException(
                                "Ticket with id '" + id + "' doesn't exist."
                        ));

        TicketPriority newPriority =
                mapper.mapTicketPriorityToEntity(request.getPriority());

        if (ticket.getPriority() == newPriority) {
            throw new SameTicketPriorityException(
                    "Ticket already has priority " + newPriority + "."
            );
        }
        ticket.setPriority(newPriority);
        Ticket updated = ticketRepository.save(ticket);

        return mapper.mapToResponse(updated);
    }

    @Override
    public TicketResponse assignWorker(
            Long ticketId,
            String username) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new TicketNotFoundException(
                                "Ticket with id '" + ticketId + "' doesn't exist."
                        ));

        User worker = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User with username '" + username + "' doesn't exist."
                        ));

        if (worker.getRole() != Role.WORKER) {
            throw new RuntimeException(
                    "Only maintenance workers can be assigned to tickets."
            );
        }
        ticket.setAssignedWorker(worker);

        if (ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.ASSIGNED);
        }
        Ticket updated = ticketRepository.save(ticket);

        return mapper.mapToResponse(updated);
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

    private wit.edu.inz.ticket.entity.TicketPriority calculatePriority(TicketType type) {

        return switch (type) {

            case POWER_OUTAGE,
                 WATER_LEAK,
                 HEATING_FAILURE -> wit.edu.inz.ticket.entity.TicketPriority.URGENT;

            case BROKEN_DOOR,
                 BROKEN_WINDOW,
                 APPLIANCE_REPAIR -> wit.edu.inz.ticket.entity.TicketPriority.HIGH;

            case LIGHT_FIXTURE,
                 CLOGGED_DRAIN -> wit.edu.inz.ticket.entity.TicketPriority.MEDIUM;

            default -> wit.edu.inz.ticket.entity.TicketPriority.LOW;
        };
    }
}