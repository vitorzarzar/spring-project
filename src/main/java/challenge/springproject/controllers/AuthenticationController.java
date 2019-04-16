package challenge.springproject.controllers;

import challenge.springproject.business.AuthenticationService;
import challenge.springproject.dto.input.LoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AuthenticationController {

    private AuthenticationService service;

    @Autowired
    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

    @PostMapping("/api/login")
    public ResponseEntity login(@Valid @RequestBody LoginDto loginDto) throws Exception {
        return ResponseEntity.ok(service.login(loginDto));
    }
}
