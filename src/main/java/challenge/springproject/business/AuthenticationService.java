package challenge.springproject.business;

import challenge.springproject.domain.User;
import challenge.springproject.dto.input.LoginDto;
import challenge.springproject.dto.input.PhoneDto;
import challenge.springproject.dto.output.UserOutputDto;
import challenge.springproject.exceptions.EmailNotFoundException;
import challenge.springproject.exceptions.ExpiredTokenException;
import challenge.springproject.exceptions.InvalidPasswordException;
import challenge.springproject.exceptions.InvalidTokenException;
import challenge.springproject.persistence.UserDao;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private final UserDao dao;

    private final PasswordEncoder passwordEncoder;

    private final TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    public AuthenticationService(UserDao dao, PasswordEncoder passwordEncoder, TokenAuthenticationService tokenAuthenticationService) {
        this.dao = dao;
        this.passwordEncoder = passwordEncoder;
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    public UserOutputDto login(LoginDto dto) throws Exception {
        User user = dao.findByEmail(dto.getEmail());
        if(user == null) throw new EmailNotFoundException();

        if(!passwordEncoder.matches(dto.getPassword(), user.getPassword())) throw new InvalidPasswordException();

        user.setLastLogin(LocalDate.now());
        user.setToken(tokenAuthenticationService.generateAuthentication(user));

        dao.save(user);

        return new UserOutputDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getPhones().stream().map(phone -> new PhoneDto(phone.getDdd(), phone.getNumber())).collect(Collectors.toList()),
                user.getCreated(),
                user.getLastLogin(),
                user.getToken()
        );
    }

    public Long validator(String tokenInput) throws Exception {
        try {
            String token = tokenInput.replace("Bearer ", "");
            Claims claims = tokenAuthenticationService.decodeAuthentication(token);

            //Verifica se o token está expirado
            if (claims.getExpiration().after(new Date(System.currentTimeMillis()))) throw new ExpiredTokenException();

            return Long.parseLong(claims.getSubject());
        } catch (ExpiredTokenException et){
            et.printStackTrace();
            throw et;
        } catch (JwtException e) {
            e.printStackTrace();
            throw new InvalidTokenException();
        }

    }
}
