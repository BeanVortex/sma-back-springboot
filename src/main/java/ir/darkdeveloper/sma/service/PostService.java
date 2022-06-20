package ir.darkdeveloper.sma.service;

import ir.darkdeveloper.sma.exceptions.BadRequestException;
import ir.darkdeveloper.sma.exceptions.NoContentException;
import ir.darkdeveloper.sma.model.PostModel;
import ir.darkdeveloper.sma.model.UserModel;
import ir.darkdeveloper.sma.repository.PostRepo;
import ir.darkdeveloper.sma.utils.IOUtils;
import ir.darkdeveloper.sma.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Optional;

import static ir.darkdeveloper.sma.utils.ExceptionUtils.exceptionHandlers;
import static ir.darkdeveloper.sma.utils.IOUtils.POST_IMAGE_PATH;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepo postRepo;
    private final IOUtils ioUtils;
    private final UserUtils userUtils;

    @Transactional
    public PostModel savePost(Optional<PostModel> postModel, HttpServletRequest req) {
        return exceptionHandlers(() -> {
            var post = postModel.orElseThrow(() -> new BadRequestException("Post can't be null"));
            var userId = post.getUser().getId();
            userUtils.checkUserIsSameUserForRequest(userId, req, "save");
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
        PostModel model2 = postRepo.findPostById(id).orElseThrow(() -> new NoContentException("Post not found"));
        Long likes = model2.getLikes();
        model2.setLikes(++likes);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public Page<PostModel> findAll(Pageable pageable) {
        return postRepo.findAll(pageable);
    }

    public Page<PostModel> searchPost(String content, String title, Pageable pageable) {
        return postRepo.findByContentAndTitleContains(content, title, pageable);
    }

    @Transactional
    public String deletePost(Long id, HttpServletRequest req) {
        return exceptionHandlers(() -> {
            var post = postRepo.findPostById(id)
                    .orElseThrow(() -> new NoContentException("Post not found"));
            userUtils.checkUserIsSameUserForRequest(post.getUser().getId(), req, "delete");
            ioUtils.deletePostImagesOfUser(post);
            postRepo.deleteById(id);
            return "deleted";
        });
    }

    public PostModel getOnePost(Long id) {
        return postRepo.findPostById(id).orElseThrow(() -> new NoContentException("Post not found"));
    }

    public Page<PostModel> getOneUserPosts(Long userId, Pageable pageable) {
        return postRepo.getOneUserPosts(userId, pageable);
    }


}
