package challenge.springproject.exceptions;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(Long id) {
        super("User not found " + id);
    }
}
