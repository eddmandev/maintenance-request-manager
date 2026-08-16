package wit.edu.inz.ticket.controller;

import api.model.TicketCreateRequest;
import api.model.TicketResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import wit.edu.inz.ticket.service.TicketService;

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

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketInfo(
            @PathVariable Long id) {

        return ResponseEntity.ok(ticketService.getTicketDetails(id));
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

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}