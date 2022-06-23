package ir.darkdeveloper.sma.controllers;

import ir.darkdeveloper.sma.TestUtils;
import ir.darkdeveloper.sma.model.PostModel;
import ir.darkdeveloper.sma.model.UserModel;
import ir.darkdeveloper.sma.repository.UserRepo;
import ir.darkdeveloper.sma.service.UserService;
import ir.darkdeveloper.sma.utils.JwtUtils;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@DirtiesContext
record PostControllerTest(MockMvc mockMvc,
                          JwtUtils jwtUtils,
                          UserService userService,
                          TestUtils testUtils) {

    @Autowired
    public PostControllerTest {
    }

    private static HttpHeaders authHeaders;
    private static Long userId;
    private static Long postId;
    private static String imageName;
    private static HttpServletRequest request;
    private static final String userName = "user n";
    private static final String password = "Pass!12";
    private static final String email = "email@mail.com";
    private static final String title = "some title";
    private static final String updatedTitle = "some title update";
    private static final String content = "some content";
    private static final String updatedContent = "some content update";
    private static final String likes = "10";


    @Test
    @Order(1)
    void signUpUser() {
        var user = UserModel.builder()
                .userName(userName)
                .password(password)
                .passwordRepeat(password)
                .email(email)
                .build();
        var response = mock(HttpServletResponse.class);
        userService.signUpUser(Optional.of(user), response);
        userId = user.getId();
        var userEmail = user.getEmail();
        request = testUtils.setUpHeaderAndGetReq(userEmail, userId);
        authHeaders = testUtils.getAuthHeaders(userEmail, userId);
    }

    @Test
    @Order(2)
    void savePost() throws Exception {
        var title = new MockPart("title", PostControllerTest.title.getBytes());
        var content = new MockPart("content", PostControllerTest.content.getBytes());
        var likes = new MockPart("likes", PostControllerTest.likes.getBytes());
        var file = new MockMultipartFile("file", "hello.jpg", MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes());

        mockMvc.perform(multipart("/api/post/")
                        .part(title, content, likes)
                        .file(file)
                        .headers(authHeaders)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(is(userId), Long.class))
                .andExpect(jsonPath("$.image").exists())
                .andDo(result -> {
                    var jsonObject = new JSONObject(result.getResponse().getContentAsString());
                    postId = jsonObject.getLong("id");
                    imageName = jsonObject.getString("image");
                });
    }

    @Test
    @Order(3)
    void findAll() throws Exception {
        mockMvc.perform(get("/api/post/all/")
                        .headers(authHeaders)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].id").value(is(postId), Long.class))
                .andExpect(jsonPath("$.content[0].image").exists());
    }

    @Test
    @Order(3)
    void updatePost() throws Exception {

        var title = new MockPart("title", PostControllerTest.updatedTitle.getBytes());
        var content = new MockPart("content", PostControllerTest.updatedContent.getBytes());
        var file = new MockMultipartFile("file", "hello.jpg", MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes());

        mockMvc.perform(multipart("/api/post/{id}/", postId)
                        .part(title, content)
                        .file(file)
                        .headers(authHeaders)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(is(userId), Long.class))
                .andExpect(jsonPath("$.image").exists())
                .andExpect(jsonPath("$.image").value(Matchers.not(imageName)))
                .andExpect(jsonPath("$.title").value(is(updatedTitle)))
                .andExpect(jsonPath("$.content").value(is(updatedContent)))
                .andExpect(jsonPath("$.likes").value(is(Long.valueOf(likes)), Long.class))
        ;

    }

    @Test
    @Order(4)
    void likePost() throws Exception {


        mockMvc.perform(put("/api/post/like/{id}/", postId)
                        .headers(authHeaders)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(is(userId), Long.class))
                .andExpect(jsonPath("$.image").exists())
                .andExpect(jsonPath("$.image").value(Matchers.not(imageName)))
                .andExpect(jsonPath("$.title").value(is(updatedTitle)))
                .andExpect(jsonPath("$.content").value(is(updatedContent)))
                .andExpect(jsonPath("$.likes").value(is(Long.parseLong(likes) + 1), Long.class))
        ;
    }

    @Test
    @Order(5)
    void getOnePost() throws Exception {
        mockMvc.perform(get("/api/post/{id}/", postId)
                        .headers(authHeaders)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(is(userId), Long.class))
                .andExpect(jsonPath("$.image").exists())
                .andExpect(jsonPath("$.image").value(Matchers.not(imageName)))
                .andExpect(jsonPath("$.title").value(is(updatedTitle)))
                .andExpect(jsonPath("$.content").value(is(updatedContent)))
                .andExpect(jsonPath("$.likes").value(is(Long.parseLong(likes) + 1), Long.class))
        ;
    }

    @Test
    @Order(6)
    void searchPost() throws Exception {
        mockMvc.perform(get("/api/post/search/")
                        .param("title", "som")
                        .param("content", "cont")
                        .headers(authHeaders)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].userId").value(is(userId), Long.class))
                .andExpect(jsonPath("$.content[0].image").exists())
                .andExpect(jsonPath("$.content[0].image").value(Matchers.not(imageName)))
                .andExpect(jsonPath("$.content[0].title").value(is(updatedTitle)))
                .andExpect(jsonPath("$.content[0].content").value(is(updatedContent)))
                .andExpect(jsonPath("$.content[0].likes").value(is(Long.parseLong(likes) + 1), Long.class))
        ;

    }

    @Test
    @Order(7)
    void getOneUserPosts() throws Exception {
        mockMvc.perform(get("/api/post/user/{id}/", userId)
                        .headers(authHeaders)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].userId").value(is(userId), Long.class))
                .andExpect(jsonPath("$.content[0].image").exists())
                .andExpect(jsonPath("$.content[0].image").value(Matchers.not(imageName)))
                .andExpect(jsonPath("$.content[0].title").value(is(updatedTitle)))
                .andExpect(jsonPath("$.content[0].content").value(is(updatedContent)))
                .andExpect(jsonPath("$.content[0].likes").value(is(Long.parseLong(likes) + 1), Long.class))
        ;
    }

    @Test
    @Order(8)
    void deletePost() throws Exception {
        mockMvc.perform(delete("/api/post/{id}/", postId)
                        .headers(authHeaders)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("deleted"))
        ;

    }
}