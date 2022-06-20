package ir.darkdeveloper.sma.service;

import ir.darkdeveloper.sma.exceptions.BadRequestException;
import ir.darkdeveloper.sma.model.CommentModel;
import ir.darkdeveloper.sma.model.PostModel;
import ir.darkdeveloper.sma.repository.CommentRepo;
import ir.darkdeveloper.sma.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Optional;

import static ir.darkdeveloper.sma.utils.ExceptionUtils.exceptionHandlers;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepo commentRepo;
    private final UserUtils userUtils;

    @Transactional
    public CommentModel saveComment(Optional<CommentModel> model, Long postId, HttpServletRequest req) {
        return exceptionHandlers(() -> {
            var comment = model.orElseThrow(() -> new BadRequestException("Comment can't be null"));
            comment.setPost(new PostModel(postId));
            userUtils.checkUserIsSameUserForRequest(comment.getUser().getId(), req, "save");
            return commentRepo.save(comment);
        });
    }

    @Transactional
    public String deleteComment(Long id, HttpServletRequest req) {
        return exceptionHandlers(() -> {
            var comment = commentRepo.findById(id)
                    .orElseThrow(() -> new BadRequestException("Comment can't be null"));
            userUtils.checkUserIsSameUserForRequest(comment.getUser().getId(), req, "delete");
            commentRepo.deleteById(comment.getId());
            return "Deleted";
        });

    }

    public Page<CommentModel> getPostComments(Optional<Long> postId, Pageable pageable) {
        return exceptionHandlers(() -> commentRepo.findCommentByPostId(
                postId.orElseThrow(() -> new BadRequestException("POst id can't be null")),
                pageable));
    }


}
