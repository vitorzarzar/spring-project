package challenge.springproject.controllers.user;

import challenge.springproject.business.UserService;
import challenge.springproject.controllers.UserController;
import challenge.springproject.dto.input.PhoneDto;
import challenge.springproject.dto.output.ExceptionOutputDto;
import challenge.springproject.dto.output.UserOutputDto;
import challenge.springproject.exceptions.IdInconsistentTokenException;
import challenge.springproject.exceptions.InvalidDataException;
import challenge.springproject.exceptions.OutdatedTokenException;
import challenge.springproject.exceptions.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class ProfileTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private String testToken = "Bearer token";
    private Long testId = (long) 1;

    @Test
    public void successTest() throws Exception {
        UserOutputDto userOutputDto = new UserOutputDto();
        userOutputDto.setId((long) 2);
        userOutputDto.setName("name");
        userOutputDto.setEmail("email@email.com");
        userOutputDto.setPassword("hash");
        userOutputDto.setPhones(List.of(new PhoneDto("81", "33333333")));
        userOutputDto.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        userOutputDto.setLastLogin(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        userOutputDto.setToken("token");

        Mockito.when(userService.userProfile(testToken, testId)).thenReturn(userOutputDto);

        MvcResult mvcResult = mockMvc.perform(get("/user/" + testId).header("Authorization", testToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(objectMapper.writeValueAsString(userOutputDto))
                .isEqualToIgnoringWhitespace(responseBody);
    }

    @Test
    public void userNotFoundTest() throws Exception {
        Mockito.when(userService.userProfile(testToken, testId)).thenThrow(new UserNotFoundException(testId));

        MvcResult mvcResult = mockMvc.perform(get("/user/" + testId).header("Authorization", testToken))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo("");
    }

    @Test
    public void invalidDataTest() throws Exception {
        InvalidDataException exception = new InvalidDataException();

        MvcResult mvcResult = mockMvc.perform(get("/user/" + testId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        String exceptionResponseBody = objectMapper.writeValueAsString(new ExceptionOutputDto(exception.getMessage()));

        assertThat(exceptionResponseBody)
                .isEqualToIgnoringWhitespace(responseBody);
    }

    @Test
    public void idInconsistentTest() throws Exception {
        exceptionTest(new IdInconsistentTokenException(), testToken, status().isUnauthorized());
    }

    @Test
    public void outdatedTokenTest() throws Exception {
        exceptionTest(new OutdatedTokenException(), testToken, status().isUnauthorized());
    }

    private void exceptionTest(Exception exception, String token, ResultMatcher statusResult) throws Exception {
        Mockito.when(userService.userProfile(token, testId)).thenThrow(exception);

        MvcResult mvcResult = mockMvc.perform(get("/user/" + testId).header("Authorization", token))
                .andDo(print())
                .andExpect(statusResult)
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        String exceptionResponseBody = objectMapper.writeValueAsString(new ExceptionOutputDto(exception.getMessage()));

        assertThat(exceptionResponseBody)
                .isEqualToIgnoringWhitespace(responseBody);
    }
}
