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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private TokenAuthenticationService tokenAuthenticationService;

    @MockBean
    private UserDao userDao;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void registerSuccessTest() throws Exception {
        String passwordEncoderMock = "hash";
        Mockito.when(passwordEncoder.encode("password")).thenReturn(passwordEncoderMock);

        RegisterDto registerDto = new RegisterDto();
        registerDto.setName("name");
        registerDto.setPassword("password");
        registerDto.setEmail("email@email.com");
        registerDto.setPhones(List.of(new PhoneDto("81", "33333333")));

        User newUser = new User();
        newUser.setName(registerDto.getName());
        newUser.setEmail(registerDto.getEmail());
        newUser.setPassword(passwordEncoderMock);
        newUser.setPhones(registerDto.getPhones().stream().map(phone -> new Phone(phone.getNumber(), phone.getDdd())).collect(Collectors.toList()));
        newUser.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        newUser.setLastLogin(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        Mockito.when(userDao.save(newUser)).thenReturn(newUser);

        Mockito.when(tokenAuthenticationService.generateAuthentication(newUser)).thenReturn("token");

        UserOutputDto outputDto = userService.register(registerDto);
        assertThat(outputDto).isEqualToComparingFieldByFieldRecursively(new UserOutputDto(
                newUser.getId(),
                newUser.getName(),
                newUser.getEmail(),
                newUser.getPassword(),
                registerDto.getPhones(),
                newUser.getCreated(),
                newUser.getLastLogin(),
                newUser.getToken()
        ));
    }

    @Test(expected = EmailAlreadyExistsException.class)
    public void registerEmailAlreadyExistTest() throws Exception {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setName("name");
        registerDto.setPassword("password");
        registerDto.setEmail("email@email.com");
        registerDto.setPhones(List.of(new PhoneDto("81", "33333333")));

        Mockito.when(userDao.findByEmail(registerDto.getEmail())).thenReturn(Optional.of(new User()));

        userService.register(registerDto);
    }

    @Test
    public void userProfileSuccessTest() throws Exception {
        String token = "token";
        Long id = (long) 0;
        Mockito.when(authenticationService.tokenValidator(token)).thenReturn(id);

        User user = new User();
        user.setId(id);
        user.setName("name");
        user.setEmail("email@email.com");
        user.setPassword("hash");
        user.setPhones(List.of(new PhoneDto("81", "33333333")).stream().map(phone -> new Phone(phone.getNumber(), phone.getDdd())).collect(Collectors.toList()));
        user.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        user.setLastLogin(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        user.setToken(token);

        Mockito.when(userDao.findById(id)).thenReturn(java.util.Optional.of(user));

        UserOutputDto outputDto = userService.userProfile(token, id);

        assertThat(outputDto).isEqualToComparingFieldByFieldRecursively(new UserOutputDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getPhones().stream().map(phone -> new PhoneDto(phone.getDdd(), phone.getNumber())).collect(Collectors.toList()),
                user.getCreated(),
                user.getLastLogin(),
                user.getToken()
        ));
    }

    @Test(expected = IdInconsistentTokenException.class)
    public void userProfileIdInconsistentTest() throws Exception {
        String token = "token";
        Long id = (long) 0;
        Mockito.when(authenticationService.tokenValidator(token)).thenReturn((long) 2);

        userService.userProfile(token, id);
    }

    @Test(expected = UserNotFoundException.class)
    public void userProfileUserNotFoundTest() throws Exception {
        String token = "token";
        Long id = (long) 0;
        Mockito.when(authenticationService.tokenValidator(token)).thenReturn(id);

        Mockito.when(userDao.findById(id)).thenReturn(Optional.empty());
        userService.userProfile(token, id);
    }

    @Test(expected = OutdatedTokenException.class)
    public void userProfileOutdatedTokenTest() throws Exception {
        String token = "token";
        Long id = (long) 0;
        Mockito.when(authenticationService.tokenValidator(token)).thenReturn(id);

        User user = new User();
        user.setToken("invalidToken");
        Mockito.when(userDao.findById(id)).thenReturn(Optional.of(user));

        userService.userProfile(token, id);
    }
}
