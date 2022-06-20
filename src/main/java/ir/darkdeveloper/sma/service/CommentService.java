package ir.darkdeveloper.sma.service;

import ir.darkdeveloper.sma.exceptions.BadRequestException;
import ir.darkdeveloper.sma.exceptions.ForbiddenException;
import ir.darkdeveloper.sma.exceptions.NoContentException;
import ir.darkdeveloper.sma.model.Authority;
import ir.darkdeveloper.sma.model.CommentModel;
import ir.darkdeveloper.sma.repository.CommentRepo;
import ir.darkdeveloper.sma.repository.PostRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

import static ir.darkdeveloper.sma.utils.ExceptionUtils.exceptionHandlers;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepo commentRepo;
    private final PostRepo postRepo;


    @Transactional
    public ResponseEntity<CommentModel> saveComment(Optional<CommentModel> model) {
        return exceptionHandlers(() -> {
            var comment = model.orElseThrow(() -> new BadRequestException("Comment can't be null"));
            commentRepo.save(comment);
            return ResponseEntity.ok().body(comment);
        });
    }

    public ResponseEntity<String> deleteComment(Optional<CommentModel> model) {
        return exceptionHandlers(() -> {
            var comment = model.orElseThrow(() -> new BadRequestException("Comment can't be null"));
            var auth = SecurityContextHolder.getContext().getAuthentication();

            var user = postRepo.findPostById(comment.getPost().getId())
                    .orElseThrow(() -> new NoContentException("Post not found"))
                    .getUser();

            if (auth.getName().equals(user.getEmail())
                    || auth.getAuthorities().contains(Authority.OP_DELETE_COMMENT)) {
                commentRepo.deleteById(comment.getId());
                return new ResponseEntity<>("Deleted", HttpStatus.OK);
            }
            throw new ForbiddenException("You can't do this action");
        });

    }

    public Page<CommentModel> getPostComments(Optional<Long> postId, Pageable pageable) {
        return exceptionHandlers(() -> commentRepo.findCommentByPostId(
                postId.orElseThrow(() -> new BadRequestException("POst id can't be null")),
                pageable));
    }


}
