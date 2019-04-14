package challenge.springproject.exceptions.hadlers;

import challenge.springproject.dto.output.ExceptionOutputDto;
import challenge.springproject.exceptions.InvalidPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class UnauthorizedHandler {

    @ExceptionHandler({
            InvalidPasswordException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    ExceptionOutputDto handler(Exception ex) {
        return new ExceptionOutputDto(ex.getMessage());
    }
}
