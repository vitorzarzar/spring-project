package challenge.springproject.exceptions;

public class EmailAlreadyExistsException extends Exception {

    public EmailAlreadyExistsException() {super("Email já existente");}
}
