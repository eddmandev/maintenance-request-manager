package controller;

import dao.TicketDAO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticket")
public class TicketsController {

    private final TicketDAO ticketDAO;

    public TicketsController(TicketDAO ticketDAO){
        this.ticketDAO = ticketDAO;
    }
}
