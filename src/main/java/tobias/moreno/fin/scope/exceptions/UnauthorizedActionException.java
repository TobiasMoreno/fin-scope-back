package tobias.moreno.fin.scope.exceptions;

public class UnauthorizedActionException extends  RuntimeException{
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
