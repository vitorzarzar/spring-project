package challenge.springproject.controllers.user;

import challenge.springproject.business.UserService;
import challenge.springproject.controllers.UserController;
import challenge.springproject.dto.input.PhoneDto;
import challenge.springproject.dto.input.RegisterDto;
import challenge.springproject.dto.output.ExceptionOutputDto;
import challenge.springproject.dto.output.UserOutputDto;
import challenge.springproject.exceptions.EmailAlreadyExistsException;
import challenge.springproject.exceptions.InvalidDataException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class RegisterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private ObjectWriter objectWriter;

    private RegisterDto testRegisterDto;

    @Before
    public void setup() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        objectWriter = mapper.writer().withDefaultPrettyPrinter();

        testRegisterDto = new RegisterDto();
        testRegisterDto.setName("name");
        testRegisterDto.setPassword("password");
        testRegisterDto.setEmail("email@email.com");
        testRegisterDto.setPhones(List.of(new PhoneDto("81", "33333333")));
    }

    @Test
    public void successTest() throws Exception {
        UserOutputDto userOutputDto = new UserOutputDto();
        userOutputDto.setId((long) 2);
        userOutputDto.setName(testRegisterDto.getName());
        userOutputDto.setEmail(testRegisterDto.getEmail());
        userOutputDto.setPassword("hash");
        userOutputDto.setPhones(testRegisterDto.getPhones());
        userOutputDto.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        userOutputDto.setLastLogin(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        userOutputDto.setToken("token");

        when(userService.register(ArgumentMatchers.isA(RegisterDto.class))).thenReturn(userOutputDto);

        MvcResult mvcResult = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(objectWriter.writeValueAsString(testRegisterDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(objectMapper.writeValueAsString(userOutputDto))
                .isEqualToIgnoringWhitespace(responseBody);
    }

    @Test
    public void invalidDataTest() throws Exception {
        exceptionTest(new InvalidDataException(), new RegisterDto(), status().isBadRequest());
    }

    @Test
    public void emailAlreadyExistTest() throws Exception {
        exceptionTest(new EmailAlreadyExistsException(), testRegisterDto, status().isBadRequest());
    }

    private void exceptionTest(Exception exception, RegisterDto registerDto, ResultMatcher statusResult) throws Exception {
        when(userService.register(ArgumentMatchers.isA(RegisterDto.class))).thenThrow(exception);

        MvcResult mvcResult = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(new ObjectMapper().writeValueAsString(registerDto)))
                .andDo(print())
                .andExpect(statusResult)
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        String exceptionResponseBody = objectMapper.writeValueAsString(new ExceptionOutputDto(exception.getMessage()));

        assertThat(exceptionResponseBody)
                .isEqualToIgnoringWhitespace(responseBody);
    }
}
