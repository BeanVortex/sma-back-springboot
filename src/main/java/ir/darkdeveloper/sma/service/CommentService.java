package ir.darkdeveloper.sma.service;

import ir.darkdeveloper.sma.exceptions.BadRequestException;
import ir.darkdeveloper.sma.exceptions.ForbiddenException;
import ir.darkdeveloper.sma.exceptions.InternalException;
import ir.darkdeveloper.sma.model.Authority;
import ir.darkdeveloper.sma.model.CommentModel;
import ir.darkdeveloper.sma.repository.CommentRepo;
import ir.darkdeveloper.sma.repository.PostRepo;
import ir.darkdeveloper.sma.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepo commentRepo;
    private final PostRepo postRepo;
    private final UserRepo userRepo;


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

            var userModel = userRepo
                    .findUserById(postRepo.findPostById(comment.getPost().getId()).getUser().getId());

            if (auth.getName().equals(userModel.getEmail())
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

    private <T> T exceptionHandlers(Supplier<T> sup) {
        try {
            return sup.get();
        } catch (ForbiddenException e) {
            throw new ForbiddenException(e);
        } catch (BadRequestException e) {
            throw new BadRequestException(e);
        } catch (Exception e) {
            throw new InternalException(e);
        }
    }
}
