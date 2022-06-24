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
            var userId = userUtils.checkUserIsSameUserForRequest(null, req, "save");
            postModel.map(PostModel::getId).ifPresent(id -> post.setId(null));
            post.setUser(new UserModel(userId));
            ioUtils.saveFile(post.getFile(), POST_IMAGE_PATH).ifPresent(post::setImage);
            return postRepo.save(post);
        });
    }

    @Transactional
    public PostModel updatePost(Optional<PostModel> model, Long id, HttpServletRequest req) {
        return exceptionHandlers(() -> {
            var post = model.orElseThrow(() -> new BadRequestException("Post can't be null"));
            var foundPost = postRepo.findById(id)
                    .orElseThrow(() -> new BadRequestException("Post with this id, does not exists"));
            var userId = foundPost.getUser().getId();
            userUtils.checkUserIsSameUserForRequest(userId, req, "update");
            foundPost.update(post);
            ioUtils.updatePostFile(foundPost);
            foundPost.setUser(new UserModel(userId));
            return postRepo.save(foundPost);
        });
    }

    @Transactional
    public PostModel likePost(Long postId) {
        var post = postRepo.findById(postId)
                .orElseThrow(() -> new NoContentException("Post not found"));
        var likes = post.getLikes();
        post.setLikes(++likes);
        return postRepo.save(post);
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
            var post = postRepo.findById(id)
                    .orElseThrow(() -> new NoContentException("Post not found"));
            userUtils.checkUserIsSameUserForRequest(post.getUser().getId(), req, "delete");
            ioUtils.deletePostImagesOfUser(post);
            postRepo.deleteById(id);
            return "deleted";
        });
    }

    public PostModel getOnePost(Long id) {
        return postRepo.findById(id).orElseThrow(() -> new NoContentException("Post not found"));
    }

    public Page<PostModel> getOneUserPosts(Long userId, Pageable pageable) {
        return postRepo.getOneUserPosts(userId, pageable);
    }


}
