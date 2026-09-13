package wit.edu.inz.ticket.controller;

import api.model.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import wit.edu.inz.ticket.service.TicketService;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketsController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> createNewTicket(
            @Valid @RequestBody TicketCreateRequest ticketRequest,
            Authentication authentication) {

        TicketResponse ticketResponse = ticketService.createTicketFromRequest(
                ticketRequest,
                authentication.getName()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(ticketResponse);
    }

    @GetMapping("/unassigned")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<List<TicketResponse>> getUnassignedTickets(
            Authentication authentication) {

        return ResponseEntity.ok(
                ticketService.getUnassignedTickets(
                        authentication.getName()
                )
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketInfo(
            @PathVariable Long id, Authentication authentication) {

        return ResponseEntity.ok(
                ticketService.getTicketDetails(id, authentication.getName())
        );
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getTickets(
            Authentication authentication) {

        List<TicketResponse> tickets = ticketService.getTicketsForUser(
                authentication.getName()
        );

        return ResponseEntity.ok(tickets);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TicketResponse> updateTicketDetails(
            @PathVariable Long id,
            @Valid @RequestBody TicketUpdateRequest request,
            Authentication authentication) {

        TicketResponse response = ticketService.editTicket(
                id,
                request,
                authentication.getName()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/follow-up")
    public ResponseEntity<TicketResponse> createFollowUpTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketCreateRequest ticketRequest,
            Authentication authentication) {

        TicketResponse response = ticketService.createFollowUpTicket(
                id,
                ticketRequest,
                authentication.getName()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateTicketStatus(
            @PathVariable Long id,
            @RequestBody @Valid TicketStatusUpdateRequest request, Authentication authentication) {

        return ResponseEntity.ok(
                ticketService.updateTicketStatus(request, id, authentication.getName())
        );
    }

    @PatchMapping("/{id}/priority")
    public ResponseEntity<TicketResponse> updateTicketPriority(
            @PathVariable Long id,
            @RequestBody @Valid TicketUpdatePriorityRequest request, Authentication authentication) {

        return ResponseEntity.ok(
                ticketService.updateTicketPriority(request, id, authentication.getName())
        );
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<TicketResponse> assignWorker(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                ticketService.assignWorker(
                        id,
                        authentication.getName()
                )
        );
    }
}