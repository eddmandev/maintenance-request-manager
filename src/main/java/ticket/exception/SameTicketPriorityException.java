package ticket.exception;

public class SameTicketPriorityException extends RuntimeException {
    public SameTicketPriorityException(String msg){
        super(msg);
    }
}
