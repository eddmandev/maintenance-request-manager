package ticket.exception;

public class TicketNotFoundException extends Exception {
    public TicketNotFoundException(String msg){
        super(msg);
    }
}