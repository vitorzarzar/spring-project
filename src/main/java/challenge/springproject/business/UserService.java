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
import challenge.springproject.persistence.PhoneDao;
import challenge.springproject.persistence.UserDao;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserDao userDao;

    private final PhoneDao phoneDao;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationService authenticationService;

    private final TokenService tokenService;

    public UserService(UserDao userDao, PhoneDao phoneDao, PasswordEncoder passwordEncoder, AuthenticationService authenticationService, TokenService tokenService) {
        this.userDao = userDao;
        this.phoneDao = phoneDao;
        this.passwordEncoder = passwordEncoder;
        this.authenticationService = authenticationService;
        this.tokenService = tokenService;
    }

    public UserOutputDto register(RegisterDto dto) throws Exception {
        if(userDao.findByEmail(dto.getEmail()).isPresent()) throw new EmailAlreadyExistsException();

        User newUser = new User();
        newUser.setName(dto.getName());
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setPhones(new ArrayList<>());
        newUser.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        newUser.setLastLogin(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        userDao.save(newUser);

        dto.getPhones().forEach(phone -> phoneDao.save(new Phone(phone.getNumber(), phone.getDdd(), newUser)) );

        newUser.setToken(tokenService.generateToken(newUser));
        userDao.save(newUser);

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
        Long tokenId = authenticationService.tokenValidator(token);
        if(!tokenId.equals(id)) throw new IdInconsistentTokenException();

        User user = userDao.findById(id).orElseThrow(UserNotFoundException::new);

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
