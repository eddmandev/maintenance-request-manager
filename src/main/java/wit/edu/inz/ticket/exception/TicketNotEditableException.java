package wit.edu.inz.ticket.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TicketNotEditableException extends RuntimeException {
    public TicketNotEditableException(String message){
        super(message);
    }
}
