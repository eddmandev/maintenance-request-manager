package controller;

import dao.TicketDAO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticket")
@AllArgsConstructor
public class TicketsController {

    private final TicketDAO ticketDAO;


}
