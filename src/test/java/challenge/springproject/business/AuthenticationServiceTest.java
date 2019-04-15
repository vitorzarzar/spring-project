package challenge.springproject.business;

import challenge.springproject.domain.Phone;
import challenge.springproject.domain.User;
import challenge.springproject.dto.input.LoginDto;
import challenge.springproject.dto.input.PhoneDto;
import challenge.springproject.dto.output.UserOutputDto;
import challenge.springproject.exceptions.EmailNotFoundException;
import challenge.springproject.exceptions.InvalidPasswordException;
import challenge.springproject.persistence.UserDao;
import org.junit.Before;
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
public class AuthenticationServiceTest {

    @Autowired
    AuthenticationService authenticationService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserDao userDao;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private LoginDto testLoginDto;
    private String testEmail = "email@email.com";
    private String testPassword = "password";

    @Before
    public void setup() {
        testLoginDto = new LoginDto(testEmail, testPassword);
    }

    @Test
    public void loginSuccessTest() throws Exception {
        String token = "token";

        User user = new User();
        user.setId((long) 1);
        user.setName("name");
        user.setEmail(testEmail);
        user.setPassword("hash");
        user.setPhones(List.of(new PhoneDto("81", "33333333")).stream().map(phone -> new Phone(phone.getNumber(), phone.getDdd())).collect(Collectors.toList()));
        user.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        user.setLastLogin(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        user.setToken(token);

        Mockito.when(userDao.findByEmail(testEmail)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(testLoginDto.getPassword(), user.getPassword())).thenReturn(true);
        Mockito.when(tokenService.generateToken(user)).thenReturn(token);

        UserOutputDto outputDto = authenticationService.login(testLoginDto);

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

    @Test(expected = EmailNotFoundException.class)
    public void loginEmailNotFoundTest() throws Exception {
        Mockito.when(userDao.findByEmail(testEmail)).thenReturn(Optional.empty());

        authenticationService.login(testLoginDto);
    }

    @Test(expected = InvalidPasswordException.class)
    public void loginInvalidPasswordTest() throws Exception {
        User user = new User();
        user.setPassword(testPassword);

        Mockito.when(userDao.findByEmail(testEmail)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(testLoginDto.getPassword(), user.getPassword())).thenReturn(false);

        authenticationService.login(testLoginDto);

    }
}
