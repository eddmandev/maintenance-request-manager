package ticket.exception;

public class UnchangedTicketStatusException extends Exception{

    public UnchangedTicketStatusException(String msg){
        super(msg);
    }
}
