package challenge.springproject.exceptions;

public class InvalidPasswordException extends Exception {

    public InvalidPasswordException() {super("Usuário e/ou senha inválidos");}
}
