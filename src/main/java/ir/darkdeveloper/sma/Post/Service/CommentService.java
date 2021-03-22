package ir.darkdeveloper.sma.Post.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ir.darkdeveloper.sma.Post.Models.CommentModel;
import ir.darkdeveloper.sma.Post.Repo.CommentRepo;
import ir.darkdeveloper.sma.Post.Repo.PostRepo;
import ir.darkdeveloper.sma.Users.Models.Authority;
import ir.darkdeveloper.sma.Users.Models.UserModel;
import ir.darkdeveloper.sma.Users.Repo.UserRepo;

@Service
public class CommentService {
    private final CommentRepo commentRepo;
    private final PostRepo postRepo;
    private final UserRepo userRepo;

    @Autowired
    public CommentService(CommentRepo commentRepo, PostRepo postRepo, UserRepo userRepo) {
        this.commentRepo = commentRepo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
    }

    @PreAuthorize("authentication.name != 'anonymousUser'")
    public ResponseEntity<?> saveComment(CommentModel model) {
        try {
            commentRepo.save(model);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> deleteComment(CommentModel comment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        UserModel userModel = userRepo
                .findUserById(postRepo.findPostById(comment.getPost().getId()).getUser().getId());

        if (auth.getName().equals(userModel.getEmail())
                || auth.getAuthorities().contains(Authority.OP_DELETE_COMMENT)) {
            try {
                commentRepo.deleteById(Long.valueOf(comment.getId()));
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("You can't do this action", HttpStatus.FORBIDDEN);

    }

    //TODO
    @PreAuthorize("authentication.name != 'anonymousUser'")
    public ResponseEntity<?> newLike(CommentModel model) {
        Long id = model.getId();
        CommentModel model2 = commentRepo.findById(id.intValue());
        Long likes = model2.getLikes();
        model2.setLikes(++likes);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
