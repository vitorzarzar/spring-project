package challenge.springproject.exceptions.hadlers;

import challenge.springproject.dto.output.ExceptionOutputDto;
import challenge.springproject.exceptions.EmailAlreadyExistsException;
import challenge.springproject.exceptions.EmailNotFoundException;
import challenge.springproject.exceptions.InvalidTokenException;
import challenge.springproject.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class BadRequestHandler {

    @ExceptionHandler({
            EmailAlreadyExistsException.class,
            EmailNotFoundException.class,
            UserNotFoundException.class,
            InvalidTokenException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ExceptionOutputDto handler(Exception ex) {
        return new ExceptionOutputDto(ex.getMessage());
    }
}

