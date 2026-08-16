package wit.edu.inz.ticket.exception;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(String msg){
        super(msg);
    }
}