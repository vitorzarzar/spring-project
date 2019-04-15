package challenge.springproject.controllers;

import challenge.springproject.business.UserService;
import challenge.springproject.dto.input.RegisterDto;
import challenge.springproject.dto.output.UserOutputDto;
import challenge.springproject.exceptions.InvalidDataException;
import challenge.springproject.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity register(@Valid @RequestBody RegisterDto registerDto) throws Exception {
        return ResponseEntity.ok(userService.register(registerDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity userProfile(HttpServletRequest request, @PathVariable Long id) throws Exception {
        String authorization = request.getHeader("Authorization");
        if(authorization == null) throw new InvalidDataException();

        try {
            UserOutputDto foundUser = userService.userProfile(authorization, id);
            return ResponseEntity.ok(foundUser);
        } catch (UserNotFoundException unf) {
            return ResponseEntity.noContent().build();
        }
    }
}
