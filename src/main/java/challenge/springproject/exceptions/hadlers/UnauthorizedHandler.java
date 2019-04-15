package challenge.springproject.exceptions.hadlers;

import challenge.springproject.dto.output.ExceptionOutputDto;
import challenge.springproject.exceptions.ExpiredTokenException;
import challenge.springproject.exceptions.IdInconsistentTokenException;
import challenge.springproject.exceptions.InvalidPasswordException;
import challenge.springproject.exceptions.OutdatedTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class UnauthorizedHandler {

    @ExceptionHandler({
            ExpiredTokenException.class,
            IdInconsistentTokenException.class,
            InvalidPasswordException.class,
            OutdatedTokenException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    ExceptionOutputDto handler(Exception ex) {
        return new ExceptionOutputDto(ex.getMessage());
    }
}
