package wit.edu.inz.ticket.exception;

public class TicketClosedException extends RuntimeException{
    public TicketClosedException(String msg){
        super(msg);
    }
}
