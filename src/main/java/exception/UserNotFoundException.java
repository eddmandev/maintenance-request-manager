package exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserNotFoundExceptrion extends Exception {
    public UserNotFoundExceptrion(String message){
        super(message);
    }
}
