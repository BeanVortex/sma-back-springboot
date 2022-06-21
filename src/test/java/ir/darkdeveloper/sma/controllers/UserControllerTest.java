package ir.darkdeveloper.sma.controllers;

import ir.darkdeveloper.sma.dto.LoginDto;
import ir.darkdeveloper.sma.utils.JwtUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Part;

import static ir.darkdeveloper.sma.TestUtils.mapToJson;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public record UserControllerTest(WebApplicationContext webApplicationContext,
                                 JwtUtils jwtUtils) {


    @Autowired
    public UserControllerTest {
    }

    private static MockMvc mockMvc;
    private static String signupRefreshToken;
    private static Long userId;
    private static String signupAccessToken;
    private static final String userName = "user n";
    private static final String updatedUserName = "user n";
    private static final String password = "Pass!12";
    private static final String email = "email@mail.com";


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Order(1)
    void signUpUser() throws Exception {
        var username = new MockPart("userName", userName.getBytes());
        var password = new MockPart("password", UserControllerTest.password.getBytes());
        var passwordRepeat = new MockPart("passwordRepeat", UserControllerTest.password.getBytes());
        var email = new MockPart("email", UserControllerTest.email.getBytes());
        var parts = new Part[]{email, username, password, passwordRepeat};
        var file1 = new MockMultipartFile("profileFile", "hello.jpg", MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes());

        mockMvc.perform(multipart("/api/user/signup/")
                        .part(parts)
                        .file(file1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(result -> {
                    signupRefreshToken = result.getResponse().getHeader("refresh_token");
                    signupAccessToken = result.getResponse().getHeader("access_token");
                    var obj = new JSONObject(result.getResponse().getContentAsString());
                    userId = obj.getLong("id");
                })
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.profilePicture").exists())
                .andExpect(jsonPath("$.id").isNotEmpty())
        ;
    }

    @Test
    @Order(2)
    void loginUser() throws Exception {
        var loginDto = new LoginDto(userName, password);
        mockMvc.perform(post("/api/user/login/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapToJson(loginDto)))
                .andDo(print())
                .andDo(result -> {
                    signupRefreshToken = result.getResponse().getHeader("refresh_token");
                    signupAccessToken = result.getResponse().getHeader("access_token");
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @Order(3)
    @WithMockUser(authorities = "OP_EDIT_USER")
    void updateUser() throws Exception {
        var username = new MockPart("userName", updatedUserName.getBytes());
        var id = new MockPart("id", String.valueOf(userId).getBytes());

        mockMvc.perform(multipart("/api/user/update/")
                        .part(username, id)
                        .header("refresh_token", signupRefreshToken)
                        .header("access_token", signupAccessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(is(userId), Long.class))
                .andExpect(jsonPath("$.userName").value(is(updatedUserName)));
    }


    @Test
    @Order(4)
    @WithMockUser(authorities = "OP_ACCESS_USER")
    void findAll() throws Exception {
        mockMvc.perform(get("/api/user/all/")
                        .header("refresh_token", signupRefreshToken)
                        .header("access_token", signupAccessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].userName").value(is(updatedUserName)))
                .andExpect(jsonPath("$.content[0].email").value(is(email)))
                .andExpect(jsonPath("$.content[0].id").value(is(userId), Long.class));
    }

    @Test
    @Order(5)
    @WithMockUser(authorities = "OP_ACCESS_USER")
    void getUserInfo() throws Exception {

        mockMvc.perform(get("/api/user/{id}/", userId)
                        .header("refresh_token", signupRefreshToken)
                        .header("access_token", signupAccessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id").value(is(userId), Long.class));
    }

    @Test
    @Order(6)
    @WithMockUser(authorities = "OP_DELETE_USER")
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/user/{id}/", userId)
                        .header("refresh_token", signupRefreshToken)
                        .header("access_token", signupAccessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(is("deleted")))
                .andDo(print());
    }

}