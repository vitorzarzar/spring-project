package challenge.springproject.controllers;

import challenge.springproject.business.UserService;
import challenge.springproject.domain.User;
import challenge.springproject.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody User user) {
        User returnUser = userService.create(user);
        return ResponseEntity.ok(returnUser);
    }

    @GetMapping
    public ResponseEntity listAll() {
        List<User> userList = userService.getAll();
        if(userList.isEmpty()) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok(userList);
    }

    @GetMapping("/{id}")
    public ResponseEntity getOne(@PathVariable Long id) throws UserNotFoundException {
        User foundUser = userService.getOne(id);
        if(foundUser == null) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok(foundUser);
    }
}
