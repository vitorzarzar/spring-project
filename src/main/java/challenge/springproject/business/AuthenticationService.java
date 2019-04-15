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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private final UserDao dao;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    public AuthenticationService(UserDao dao, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.dao = dao;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public UserOutputDto login(LoginDto dto) throws Exception {
        User user = dao.findByEmail(dto.getEmail()).orElseThrow(EmailNotFoundException::new);

        if(!passwordEncoder.matches(dto.getPassword(), user.getPassword())) throw new InvalidPasswordException();

        user.setLastLogin(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        user.setToken(tokenService.generateToken(user));

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

    public Long tokenValidator(String tokenInput) throws Exception {
        try {
            String token = tokenInput.replace("Bearer ", "");
            Claims claims = tokenService.decodeToken(token);

            //Verifica se o token está expirado
            if (claims.getExpiration().before(new Date(System.currentTimeMillis()))) throw new ExpiredTokenException();

            return Long.parseLong(claims.getSubject());
        } catch (ExpiredTokenException et){
            et.printStackTrace();
            throw et;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidTokenException();
        }

    }
}
