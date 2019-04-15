package challenge.springproject.exceptions.hadlers;

import challenge.springproject.dto.output.ExceptionOutputDto;
import challenge.springproject.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
            InvalidTokenException.class,
            InvalidDataException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ExceptionOutputDto handler(Exception ex) {
        return new ExceptionOutputDto(ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ExceptionOutputDto handlerDefaultMessage(Exception e) {
        return new ExceptionOutputDto(new InvalidDataException().getMessage());
    }
}

