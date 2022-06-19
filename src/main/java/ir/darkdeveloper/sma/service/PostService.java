package ir.darkdeveloper.sma.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import ir.darkdeveloper.sma.exceptions.BadRequestException;
import ir.darkdeveloper.sma.exceptions.ForbiddenException;
import ir.darkdeveloper.sma.exceptions.InternalException;
import ir.darkdeveloper.sma.exceptions.NoContentException;
import ir.darkdeveloper.sma.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ir.darkdeveloper.sma.model.PostModel;
import ir.darkdeveloper.sma.repository.PostRepo;
import ir.darkdeveloper.sma.model.Authority;
import ir.darkdeveloper.sma.model.UserModel;
import ir.darkdeveloper.sma.repository.UserRepo;
import ir.darkdeveloper.sma.utils.IOUtils;
import ir.darkdeveloper.sma.utils.UserUtils;

import static ir.darkdeveloper.sma.utils.Generics.exceptionHandlers;
import static ir.darkdeveloper.sma.utils.IOUtils.POST_IMAGE_PATH;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepo postRepo;
    private final UserRepo userRepo;
    private final IOUtils ioUtils;
    private final UserUtils userUtils;
    private final RefreshService refreshService;
    private final JwtUtils jwtUtils;

    @Transactional
    public PostModel savePost(Optional<PostModel> postModel, HttpServletRequest req) {
        return exceptionHandlers(() -> {
            var userId = checkUserIsSameUserForRequest(req, "save");
            var post = postModel.orElseThrow(() -> new BadRequestException("Post can't be null"));
            postModel.map(PostModel::getId).ifPresent(id -> post.setId(null));
            post.setUser(new UserModel(userId));
            ioUtils.saveFile(post.getFile(), POST_IMAGE_PATH).ifPresent(post::setImage);
            return postRepo.save(post);
        });
    }

    // TODO
    @PreAuthorize("authentication.name != 'anonymousUser'")
    public ResponseEntity<?> newLike(PostModel model) {

        Long id = model.getId();
        PostModel model2 = postRepo.findPostById(id);
        Long likes = model2.getLikes();
        model2.setLikes(++likes);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public Page<PostModel> allPosts(Pageable pageable) {
        return postRepo.findAll(pageable);
    }

    public Page<PostModel> searchPost(String content, String title, Pageable pageable) {
        return postRepo.findByContentAndTitleContains(content, title, pageable);
    }

    @Transactional
    public ResponseEntity<?> deletePost(HttpServletRequest request, PostModel post) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        userUtils.setUserIdForPost(request, post);
        var userModel = userRepo.findUserById(post.getUser().getId())
                .orElseThrow(() -> new NoContentException("User not found"));
        if (auth.getName().equals(userModel.getEmail()) || auth.getAuthorities().contains(Authority.OP_DELETE_POST)) {
            try {
                PostModel model = postRepo.findPostById(post.getId());

                Files.delete(Paths.get(ioUtils.getImagePath(model, POST_IMAGE_PATH)));
                postRepo.deleteById(post.getId());
                return new ResponseEntity<>(HttpStatus.OK);

            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("You can't do this action", HttpStatus.FORBIDDEN);
        }
    }

    public PostModel getOnePost(Long id) {
        return postRepo.findPostById(id);
    }

    public Page<PostModel> getOneUserPosts(Long userId, Pageable pageable) {

        return postRepo.getOneUserPosts(userId, pageable);
    }


    private Long checkUserIsSameUserForRequest(HttpServletRequest req, String operation) {
        var token = req.getHeader("refresh_token");
        if (!jwtUtils.isTokenExpired(token)) {
            var id = jwtUtils.getUserId(token);
            // db query
            var fetchedId = refreshService.getUserIdByRefreshToken(token);
            if (!fetchedId.equals(id))
                throw new ForbiddenException("You can't " + operation + " another user's posts");
            return id;
        } else
            throw new ForbiddenException("You are logged out. Try logging in again");
    }

}
