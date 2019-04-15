package challenge.springproject.controllers;

import challenge.springproject.business.AuthenticationService;
import challenge.springproject.dto.input.LoginDto;
import challenge.springproject.dto.input.PhoneDto;
import challenge.springproject.dto.output.ExceptionOutputDto;
import challenge.springproject.dto.output.UserOutputDto;
import challenge.springproject.exceptions.EmailNotFoundException;
import challenge.springproject.exceptions.InvalidDataException;
import challenge.springproject.exceptions.InvalidPasswordException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthenticationControllerTest.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    private ObjectWriter objectWriter;
    private LoginDto testLoginDto;
    private String testEmail = "email@email.com";
    private String testPassword = "password";

    @Before
    public void setup() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        objectWriter = mapper.writer().withDefaultPrettyPrinter();

        testLoginDto = new LoginDto(testEmail, testPassword);
    }

    @Test
    public void loginSuccessTest() throws Exception {

        UserOutputDto userOutputDto = new UserOutputDto();
        userOutputDto.setId((long) 2);
        userOutputDto.setName("name");
        userOutputDto.setEmail(testEmail);
        userOutputDto.setPassword(testPassword);
        userOutputDto.setPhones(List.of(new PhoneDto("81", "33333333")));
        userOutputDto.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        userOutputDto.setLastLogin(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        userOutputDto.setToken("token");

        Mockito.when(authenticationService.login(testLoginDto)).thenReturn(userOutputDto);

        MvcResult mvcResult = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(objectWriter.writeValueAsString(testLoginDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(objectMapper.writeValueAsString(userOutputDto))
                .isEqualToIgnoringWhitespace(actualResponseBody);
    }

    @Test
    public void loginInvalidDataTest() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(objectWriter.writeValueAsString(new LoginDto())))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(new ExceptionOutputDto(new InvalidDataException().getMessage()));

        assertThat(expectedResponseBody)
                .isEqualToIgnoringWhitespace(actualResponseBody);
    }

    @Test
    public void loginEmailNotFoundTest() throws Exception {
        EmailNotFoundException exception = new EmailNotFoundException();

        Mockito.when(authenticationService.login(testLoginDto)).thenThrow(exception);

        MvcResult mvcResult = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(objectWriter.writeValueAsString(testLoginDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(new ExceptionOutputDto(exception.getMessage()));

        assertThat(expectedResponseBody)
                .isEqualToIgnoringWhitespace(actualResponseBody);
    }

    @Test
    public void loginInvalidPasswordTest() throws Exception {
        InvalidPasswordException exception = new InvalidPasswordException();

        Mockito.when(authenticationService.login(testLoginDto)).thenThrow(exception);

        MvcResult mvcResult = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(objectWriter.writeValueAsString(testLoginDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(new ExceptionOutputDto(exception.getMessage()));

        assertThat(expectedResponseBody)
                .isEqualToIgnoringWhitespace(actualResponseBody);
    }
}
