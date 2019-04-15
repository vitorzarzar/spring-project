package challenge.springproject.business;

import challenge.springproject.domain.Phone;
import challenge.springproject.domain.User;
import challenge.springproject.dto.input.LoginDto;
import challenge.springproject.dto.input.PhoneDto;
import challenge.springproject.dto.output.UserOutputDto;
import challenge.springproject.exceptions.EmailNotFoundException;
import challenge.springproject.exceptions.InvalidPasswordException;
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
public class AuthenticationServiceTest {

    @Autowired
    AuthenticationService authenticationService;

    @MockBean
    private TokenAuthenticationService tokenAuthenticationService;

    @MockBean
    private UserDao userDao;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void loginSuccessTest() throws Exception {
        String email = "email@email.com";
        String password = "password";
        String token = "token";

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(email);
        loginDto.setPassword(password);

        User user = new User();
        user.setId((long) 1);
        user.setName("name");
        user.setEmail(email);
        user.setPassword("hash");
        user.setPhones(List.of(new PhoneDto("81", "33333333")).stream().map(phone -> new Phone(phone.getNumber(), phone.getDdd())).collect(Collectors.toList()));
        user.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        user.setLastLogin(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        user.setToken(token);

        Mockito.when(userDao.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(true);
        Mockito.when(tokenAuthenticationService.generateAuthentication(user)).thenReturn(token);

        UserOutputDto outputDto = authenticationService.login(loginDto);

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
        String email = "email@email.com";
        String password = "password";

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(email);
        loginDto.setPassword(password);

        Mockito.when(userDao.findByEmail(email)).thenReturn(Optional.empty());

        authenticationService.login(loginDto);
    }

    @Test(expected = InvalidPasswordException.class)
    public void loginInvalidPasswordTest() throws Exception {
        String email = "email@email.com";
        String password = "password";

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(email);
        loginDto.setPassword(password);

        User user = new User();
        user.setPassword("hash");

        Mockito.when(userDao.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(false);

        authenticationService.login(loginDto);

    }
}
