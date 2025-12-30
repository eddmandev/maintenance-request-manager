package service;

import entity.Ticket;

public interface TicketService {

    Ticket createTicketFromRequest();

    void closeTicket();

    void updateTicketStatus();
}
