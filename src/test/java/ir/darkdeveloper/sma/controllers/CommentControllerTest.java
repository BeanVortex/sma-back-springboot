package ir.darkdeveloper.sma.controllers;

import ir.darkdeveloper.sma.TestUtils;
import ir.darkdeveloper.sma.model.CommentModel;
import ir.darkdeveloper.sma.model.PostModel;
import ir.darkdeveloper.sma.model.UserModel;
import ir.darkdeveloper.sma.repository.UserRepo;
import ir.darkdeveloper.sma.service.CommentService;
import ir.darkdeveloper.sma.service.PostService;
import ir.darkdeveloper.sma.service.UserService;
import ir.darkdeveloper.sma.utils.JwtUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static ir.darkdeveloper.sma.TestUtils.mapToJson;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
record CommentControllerTest(MockMvc mockMvc,
                             JwtUtils jwtUtils,
                             UserService userService,
                             UserRepo userRepo,
                             TestUtils testUtils,
                             PostService postService,
                             CommentService commentService) {

    @Autowired
    public CommentControllerTest {
    }

    private static HttpHeaders authHeaders;
    private static Long userId;
    private static Long postId;
    private static Long commentId;

    private static final String userName = "user n";
    private static final String password = "Pass!12";
    private static final String email = "email@mail.com";
    private static final String postTitle = "some title";
    private static final String postContent = "some content";
    private static final String postLikes = "10";
    private static final String commentContent = "some comment content";
    private static final String updatedCommentContent = "some updated comment content";
    private static final String commentLikes = "16";


    @BeforeEach
    void setUp() {
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
        var request = testUtils.setUpHeaderAndGetReq(userEmail, userId);
        authHeaders = testUtils.getAuthHeaders(userEmail, userId);

        var post = PostModel.builder()
                .content(postContent)
                .likes(Long.valueOf(postLikes))
                .title(postTitle)
                .build();

        var savedPost = postService.savePost(Optional.of(post), request);
        postId = savedPost.getId();

        var comment = CommentModel.builder()
                .content(commentContent)
                .likes(Long.valueOf(commentLikes))
                .build();
        var savedComment = commentService.saveComment(Optional.of(comment), postId, request);
        commentId = savedComment.getId();
    }

    @AfterEach
    void cleanUp() {
        userRepo.deleteAll();
    }


    @Test
    void saveComment() throws Exception {
        var comment = CommentModel.builder()
                .content(commentContent)
                .likes(Long.valueOf(commentLikes))
                .post(new PostModel(postId))
                .build();

        mockMvc.perform(post("/api/post/comment/{postId}/", postId)
                        .content(mapToJson(comment))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(authHeaders)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.postId").exists())
                .andExpect(jsonPath("$.postId").value(is(postId), Long.class))
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.userId").value(is(userId), Long.class))
                .andExpect(jsonPath("$.likes").value(is(Long.valueOf(commentLikes)), Long.class))
        ;

    }

    @Test
    void updateComment() throws Exception {
        var comment = CommentModel.builder()
                .content(updatedCommentContent)
                .likes(Long.valueOf(commentLikes))
                .post(new PostModel(postId))
                .build();

        mockMvc.perform(put("/api/post/comment/{commentId}/", commentId)
                        .content(mapToJson(comment))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(authHeaders)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.postId").exists())
                .andExpect(jsonPath("$.postId").value(is(postId), Long.class))
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.userId").value(is(userId), Long.class))
                .andExpect(jsonPath("$.content").value(is(updatedCommentContent)))
                .andExpect(jsonPath("$.likes").value(is(Long.valueOf(commentLikes)), Long.class))
        ;
    }

    @Test
    void likeComment() throws Exception {
        mockMvc.perform(put("/api/post/comment/like/{commentId}/", commentId)
                        .headers(authHeaders)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(is(userId), Long.class))
                .andExpect(jsonPath("$.content").value(is(commentContent)))
                .andExpect(jsonPath("$.likes").value(is(Long.parseLong(commentLikes) + 1), Long.class))
        ;
    }

    @Test
    void getPostComments() throws Exception {
        mockMvc.perform(get("/api/post/comment/{postId}/", postId)
                        .headers(authHeaders)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].postId").exists())
                .andExpect(jsonPath("$.content[0].postId").value(is(postId), Long.class))
                .andExpect(jsonPath("$.content[0].userId").exists())
                .andExpect(jsonPath("$.content[0].userId").value(is(userId), Long.class))
                .andExpect(jsonPath("$.content[0].content").value(is(commentContent)))
                .andExpect(jsonPath("$.content[0].likes").value(is(Long.parseLong(commentLikes)), Long.class))
        ;
    }

    @Test
    void deleteComment() throws Exception {
        mockMvc.perform(delete("/api/post/comment/{commentId}/", commentId)
                        .headers(authHeaders)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").value(is("deleted")))
        ;
    }
}