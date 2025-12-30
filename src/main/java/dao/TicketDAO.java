package dao;

import entity.Ticket;

public interface TicketDAO {
    void saveTicket(Ticket ticket);

    void closeTicket();

    void updateTicket();
}
