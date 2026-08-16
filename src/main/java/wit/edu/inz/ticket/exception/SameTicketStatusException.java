package wit.edu.inz.ticket.exception;

public class SameTicketStatusException extends RuntimeException{

    public SameTicketStatusException(String msg){
        super(msg);
    }
}
