package ir.darkdeveloper.sma.Post.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ir.darkdeveloper.sma.Post.Models.CommentModel;
import ir.darkdeveloper.sma.Post.Repo.CommentRepo;
import ir.darkdeveloper.sma.Post.Repo.PostRepo;
import ir.darkdeveloper.sma.Users.Models.UserModel;
import ir.darkdeveloper.sma.Users.Repo.UserRepo;
import ir.darkdeveloper.sma.Users.Service.UserService;

@Service
public class CommentService {
    private final CommentRepo commentRepo;
    private final PostRepo postRepo;
    private final UserRepo userRepo;
    private final UserService userService;

    @Autowired
    public CommentService(CommentRepo commentRepo, PostRepo postRepo, UserRepo userRepo, UserService userService) {
        this.commentRepo = commentRepo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.userService = userService;
    }

    public ResponseEntity<?> saveComment(CommentModel model) {
        try {
            UserModel userModel = userRepo.findUserById(postRepo.findById(model.getPost().getId()).getUser().getId());
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //TODO delete admin access on production
            if (auth.getName().equals(userModel.getEmail()) || auth.getName().equals(userService.getAdminUsername())){
                commentRepo.save(model);
                return new ResponseEntity<>(HttpStatus.OK);
            }else {
                return new ResponseEntity<>("You can't do this action", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> deleteComment(CommentModel comment) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserModel userModel = userRepo.findUserById(postRepo.findById(comment.getPost().getId()).getUser().getId());
            if (auth.getName().equals(userModel.getEmail()) || auth.getName().equals(userService.getAdminUsername())) {
                commentRepo.deleteById(Long.valueOf(comment.getId()));
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("You can't do this action", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /*
     * public Page<CommentModel> commentsOfPost(String id, Pageable pageable){
     * return repo.findByPost(id, pageable); }
     */
}
