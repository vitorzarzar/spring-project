package challenge.springproject.business;

import challenge.springproject.domain.Phone;
import challenge.springproject.domain.User;
import challenge.springproject.dto.input.PhoneDto;
import challenge.springproject.dto.input.RegisterDto;
import challenge.springproject.dto.output.UserOutputDto;
import challenge.springproject.exceptions.EmailAlreadyExistsException;
import challenge.springproject.exceptions.IdInconsistentTokenException;
import challenge.springproject.exceptions.OutdatedTokenException;
import challenge.springproject.exceptions.UserNotFoundException;
import challenge.springproject.persistence.UserDao;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserDao dao;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationService authenticationService;

    private final TokenAuthenticationService tokenAuthenticationService;

    public UserService(UserDao dao, PasswordEncoder passwordEncoder, AuthenticationService authenticationService, TokenAuthenticationService tokenAuthenticationService) {
        this.dao = dao;
        this.passwordEncoder = passwordEncoder;
        this.authenticationService = authenticationService;
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    public UserOutputDto register(RegisterDto dto) throws Exception {
        if(dao.findByEmail(dto.getEmail()) != null) throw new EmailAlreadyExistsException();

        User newUser = new User();
        newUser.setName(dto.getName());
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setPhones(dto.getPhones().stream().map(phone -> new Phone(phone.getNumber(), phone.getDdd())).collect(Collectors.toList()));
        newUser.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        newUser.setLastLogin(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        dao.save(newUser);

        newUser.setToken(tokenAuthenticationService.generateAuthentication(newUser));
        dao.setToken(newUser.getToken(), newUser.getId());

        return new UserOutputDto(
                newUser.getId(),
                newUser.getName(),
                newUser.getEmail(),
                newUser.getPassword(),
                dto.getPhones(),
                newUser.getCreated(),
                newUser.getLastLogin(),
                newUser.getToken()
        );
    }


    public UserOutputDto userProfile(String token, Long id) throws Exception {
        Long tokenId = authenticationService.validator(token);
        if(!tokenId.equals(id)) throw new IdInconsistentTokenException();

        User user = dao.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if(user == null) return null;

        if(!token.replace("Bearer ", "").equals(user.getToken())) throw new OutdatedTokenException();

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
}
