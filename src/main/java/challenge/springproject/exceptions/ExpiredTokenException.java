package challenge.springproject.exceptions;

public class ExpiredTokenException extends Exception {
    public ExpiredTokenException() { super("Não autorizado"); }
}
