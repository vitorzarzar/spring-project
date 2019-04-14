package challenge.springproject.exceptions;

public class OutdatedTokenException extends Exception {
    public OutdatedTokenException() { super("Não autorizado"); }
}
