package challenge.springproject.business;

import challenge.springproject.domain.Phone;
import challenge.springproject.domain.User;
import challenge.springproject.dto.input.PhoneDto;
import challenge.springproject.dto.input.RegisterDto;
import challenge.springproject.dto.output.UserOutputDto;
import challenge.springproject.exceptions.InvalidDataException;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserDao userDao;

    @MockBean
    private TokenAuthenticationService tokenAuthenticationService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private String passwordEncoderMock = "hash";

    @Test
    public void registerValidUser() throws Exception {
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

        UserOutputDto outputDto = userService.register(registerDto);
        assertThat(outputDto).isEqualToComparingFieldByField(new UserOutputDto(
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

    @Test(expected = InvalidDataException.class)
    public void registerWithoutName() throws Exception {
        Mockito.when(passwordEncoder.encode("password")).thenReturn(passwordEncoderMock);

        RegisterDto registerDto = new RegisterDto();
        registerDto.setPassword("password");
        registerDto.setEmail("email@email.com");
        registerDto.setPhones(List.of(new PhoneDto("81", "33333333")));

        UserOutputDto outputDto = userService.register(registerDto);
    }
}
