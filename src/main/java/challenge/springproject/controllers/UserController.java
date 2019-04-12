package challenge.springproject.controllers;

import challenge.springproject.business.UserService;
import challenge.springproject.domain.User;
import challenge.springproject.dto.RegisterDto;
import challenge.springproject.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity register(@RequestBody RegisterDto registerDto) {
        User returnUser = userService.create(registerDto);
        return ResponseEntity.ok(returnUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity getOne(@PathVariable Long id) throws UserNotFoundException {
        User foundUser = userService.getOne(id);
        if(foundUser == null) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok(foundUser);
    }
}
