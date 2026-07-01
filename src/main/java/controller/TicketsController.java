package controller;

import api.model.TicketCreateRequest;
import api.model.TicketResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.TicketService;

@RestController
@RequestMapping("/tickets")
public class TicketsController {

    private final TicketService ticketService;
    public TicketsController(TicketService ticketService){
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createNewTicket(TicketCreateRequest ticketRequest){
        var ticketResponse = ticketService.createTicketFromRequest(ticketRequest);
        return ResponseEntity.ok(ticketResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketInfo(@RequestParam String ticketId){
        return ResponseEntity.ok(ticketService.getTicketDetails(ticketId));
    }
}
