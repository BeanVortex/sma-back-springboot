package ir.darkdeveloper.sma.service;

import ir.darkdeveloper.sma.exceptions.BadRequestException;
import ir.darkdeveloper.sma.exceptions.ForbiddenException;
import ir.darkdeveloper.sma.exceptions.NoContentException;
import ir.darkdeveloper.sma.model.CommentModel;
import ir.darkdeveloper.sma.model.PostModel;
import ir.darkdeveloper.sma.model.UserModel;
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
            var userId = userUtils.checkUserIsSameUserForRequest(null, req, "save");
            comment.setUser(new UserModel(userId));
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
            return "deleted";
        });

    }

    @Transactional
    public CommentModel updateComment(Optional<CommentModel> model, Long commentId, HttpServletRequest req) {
        return exceptionHandlers(() -> {
            var comment = model.orElseThrow(() -> new BadRequestException("Comment can't be null"));
            var postId = model.map(CommentModel::getPost).map(PostModel::getId)
                    .orElseThrow(() -> new BadRequestException("Post id can't be null"));
            var foundComment = commentRepo.findById(commentId)
                    .orElseThrow(() -> new BadRequestException("Comment with this id, does not exists"));
            var userId = foundComment.getUser().getId();
            if (!postId.equals(foundComment.getPost().getId()))
                throw new ForbiddenException("You can't edit the comment on the other post");
            userUtils.checkUserIsSameUserForRequest(userId, req, "update");
            foundComment.update(comment);
            return commentRepo.save(foundComment);
        });
    }

    public Page<CommentModel> getPostComments(Optional<Long> postId, Pageable pageable) {
        return exceptionHandlers(() -> commentRepo.findCommentByPostId(
                postId.orElseThrow(() -> new BadRequestException("POst id can't be null")),
                pageable));
    }


    @Transactional
    public CommentModel likeComment(Long commentId) {
        var comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new NoContentException("Comment not found"));
        var likes = comment.getLikes();
        comment.setLikes(++likes);
        return commentRepo.save(comment);
    }
}
