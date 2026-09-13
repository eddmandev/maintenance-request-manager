package wit.edu.inz.ticket.service;

import api.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import wit.edu.inz.exception.UserNotFoundException;
import wit.edu.inz.role.entity.Role;
import wit.edu.inz.ticket.entity.Ticket;
import wit.edu.inz.ticket.entity.TicketPriority;
import wit.edu.inz.ticket.entity.TicketStatus;
import wit.edu.inz.ticket.exception.SameTicketPriorityException;
import wit.edu.inz.ticket.exception.SameTicketStatusException;
import wit.edu.inz.ticket.exception.TicketNotEditableException;
import wit.edu.inz.ticket.exception.TicketNotFoundException;
import wit.edu.inz.ticket.mapper.TicketApiMapper;
import wit.edu.inz.ticket.repository.TicketRepository;
import wit.edu.inz.user.entity.User;
import wit.edu.inz.user.repository.UserRepository;

import java.util.List;

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

        User creator = getUser(username);

        Ticket ticket = mapper.mapToEntity(request);

        ticket.setCreatedBy(creator);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(calculatePriority(request.getType()));

        return mapper.mapToResponse(
                ticketRepository.save(ticket)
        );
    }

    @Override
    public TicketResponse createFollowUpTicket(
            Long ticketId,
            TicketCreateRequest request,
            String username) {

        Ticket parentTicket = getTicket(ticketId);
        User currentUser = getUser(username);

        validateOwnership(parentTicket, currentUser);

        Ticket followUp = mapper.mapToEntity(request);

        followUp.setCreatedBy(currentUser);
        followUp.setParentTicket(parentTicket);
        followUp.setStatus(TicketStatus.OPEN);
        followUp.setPriority(calculatePriority(request.getType()));

        return mapper.mapToResponse(
                ticketRepository.save(followUp)
        );
    }

    @Override
    public TicketResponse updateTicketStatus(
            TicketStatusUpdateRequest request,
            long id,
            String username) {

        Ticket ticket = getTicket(id);
        User currentUser = getUser(username);

        validateAssignedWorker(ticket, currentUser);

        TicketStatus newStatus =
                mapper.mapTicketStatusToEntity(request.getStatus());

        if (ticket.getStatus() == newStatus) {
            throw new SameTicketStatusException(
                    "Ticket already has status " + newStatus + "."
            );
        }

        ticket.setStatus(newStatus);

        return mapper.mapToResponse(
                ticketRepository.save(ticket)
        );
    }

    @Override
    public TicketResponse getTicketDetails(
            long id,
            String username) {

        Ticket ticket = getTicket(id);
        User currentUser = getUser(username);

        validateTicketAccess(ticket, currentUser);

        return mapper.mapToResponse(ticket);
    }

    @Override
    public List<TicketResponse> getTicketsForUser(String username) {

        User currentUser = getUser(username);

        if (currentUser.getRole() != Role.USER) {
            throw new AccessDeniedException(
                    "Only residents can access their personal ticket list."
            );
        }

        return ticketRepository
                .findAllByCreatedByUsername(username)
                .stream()
                .map(mapper::mapToResponse)
                .toList();
    }

    @Override
    public TicketResponse updateTicketPriority(
            TicketUpdatePriorityRequest request,
            long id,
            String username) {

        Ticket ticket = getTicket(id);
        User currentUser = getUser(username);

        validateAssignedWorker(ticket, currentUser);

        TicketPriority newPriority =
                mapper.mapTicketPriorityToEntity(request.getPriority());

        if (ticket.getPriority() == newPriority) {
            throw new SameTicketPriorityException(
                    "Ticket already has priority " + newPriority + "."
            );
        }

        ticket.setPriority(newPriority);

        return mapper.mapToResponse(
                ticketRepository.save(ticket)
        );
    }

    @Override
    public TicketResponse assignWorker(
            Long ticketId,
            String username) {

        Ticket ticket = getTicket(ticketId);
        User worker = getUser(username);

        validateWorker(worker);

        if (ticket.getAssignedWorker() != null) {
            throw new IllegalStateException(
                    "Ticket is already assigned to a worker."
            );
        }

        if (ticket.getStatus() != TicketStatus.OPEN) {
            throw new IllegalStateException(
                    "Only open tickets can be assigned."
            );
        }

        ticket.setAssignedWorker(worker);
        ticket.setStatus(TicketStatus.ASSIGNED);

        return mapper.mapToResponse(
                ticketRepository.save(ticket)
        );
    }

    @Override
    public TicketResponse editTicket(
            Long ticketId,
            TicketUpdateRequest request,
            String username) {

        Ticket ticket = getTicket(ticketId);
        User currentUser = getUser(username);

        validateOwnership(ticket, currentUser);

        if (ticket.getStatus() == TicketStatus.COMPLETED) {
            throw new TicketNotEditableException(
                    "Unable to edit a completed ticket, create a new ticket."
            );
        }

        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());

        ticket.setCategory(
                mapper.mapTicketCategoryToEntity(
                        request.getCategory()
                )
        );

        ticket.setType(
                mapper.mapTicketTypeToEntity(
                        request.getType()
                )
        );

        return mapper.mapToResponse(
                ticketRepository.save(ticket)
        );
    }

    @Override
    public List<TicketResponse> getUnassignedTickets(
            String username) {

        User currentUser = getUser(username);

        validateWorker(currentUser);

        return ticketRepository
                .findAllByAssignedWorkerIsNullAndStatus(TicketStatus.OPEN)
                .stream()
                .map(mapper::mapToResponse)
                .toList();
    }

    private Ticket getTicket(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new TicketNotFoundException(
                                "Ticket with id '" + ticketId + "' doesn't exist."
                        )
                );
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User with username '" + username + "' doesn't exist."
                        )
                );
    }

    private void validateTicketAccess(
            Ticket ticket,
            User currentUser) {

        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }

        if (currentUser.getRole() == Role.USER) {
            validateOwnership(ticket, currentUser);
            return;
        }

        if (currentUser.getRole() == Role.WORKER) {
            boolean assignedToCurrentWorker =
                    ticket.getAssignedWorker() != null
                            && ticket.getAssignedWorker()
                            .getId()
                            .equals(currentUser.getId());

            boolean openAndUnassigned =
                    ticket.getAssignedWorker() == null
                            && ticket.getStatus() == TicketStatus.OPEN;

            if (assignedToCurrentWorker || openAndUnassigned) {
                return;
            }
        }

        throw new AccessDeniedException(
                "You are not allowed to access this ticket."
        );
    }

    private void validateOwnership(
            Ticket ticket,
            User currentUser) {

        if (!ticket.getCreatedBy()
                .getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "You are not allowed to access this ticket."
            );
        }
    }

    private void validateAssignedWorker(
            Ticket ticket,
            User currentUser) {

        validateWorker(currentUser);

        if (ticket.getAssignedWorker() == null) {
            throw new AccessDeniedException(
                    "Ticket is not assigned to a worker."
            );
        }

        if (!ticket.getAssignedWorker()
                .getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "Ticket is assigned to another worker."
            );
        }
    }

    private void validateWorker(User user) {
        if (user.getRole() != Role.WORKER) {
            throw new AccessDeniedException(
                    "Only maintenance workers can perform this operation."
            );
        }
    }

    private TicketPriority calculatePriority(TicketType type) {
        return switch (type) {

            case POWER_OUTAGE,
                 WATER_LEAK,
                 HEATING_FAILURE -> TicketPriority.URGENT;

            case BROKEN_DOOR,
                 BROKEN_WINDOW,
                 APPLIANCE_REPAIR -> TicketPriority.HIGH;

            case LIGHT_FIXTURE,
                 CLOGGED_DRAIN -> TicketPriority.MEDIUM;

            default -> TicketPriority.LOW;
        };
    }
}