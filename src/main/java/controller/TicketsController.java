package controller;

import api.model.TicketCreateRequest;
import api.model.TicketResponse;
import dao.TicketDAO;
import entity.Ticket;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.TicketService;

@RestController
@RequestMapping("/tickets")
public class TicketsController {

    private final TicketService ticketService;
    public TicketsController(TicketService ticketService){
        this.ticketService = ticketService;
    }

    @PostMapping("/new")
    public ResponseEntity<TicketResponse> createNewTicket(TicketCreateRequest ticketRequest){
        var ticketResponse = ticketService.createTicketFromRequest(ticketRequest);
        return new ResponseEntity.ok();
    }
}
