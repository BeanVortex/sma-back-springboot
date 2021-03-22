package ir.darkdeveloper.sma.Post.Service;

import java.nio.file.Files;
import java.nio.file.Paths;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ir.darkdeveloper.sma.Post.Models.PostModel;
import ir.darkdeveloper.sma.Post.Repo.PostRepo;
import ir.darkdeveloper.sma.Users.Models.Authority;
import ir.darkdeveloper.sma.Users.Models.UserModel;
import ir.darkdeveloper.sma.Users.Repo.UserRepo;
import ir.darkdeveloper.sma.Utils.IOUtils;

@Service
public class PostService {

    private final PostRepo postRepo;
    private final UserRepo userRepo;
    private final IOUtils ioUtils;
    private final String path = "posts/";
    private Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    @Autowired
    public PostService(PostRepo postRepo, UserRepo userRepo, IOUtils ioUtils) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.ioUtils = ioUtils;
    }

    @Transactional
    @PreAuthorize("authentication.name != 'anonymousUser'")
    public ResponseEntity<?> savePost(PostModel model) {
        if (auth.getName().equals(userRepo.findUserById(model.getUser().getId()).getEmail())
                || auth.getName().equals(userRepo.findUserById(model.getUser().getId()).getUserName())) {

            try {
                // For updating Post img by deleting previous img and replacing with new one and
                // new name

                PostModel preModel = postRepo.findById(model.getId().intValue());
                if (model.getId() != null && model.getFile() != null) {
                    Files.delete(Paths.get(ioUtils.getImagePath(preModel, path)));
                }
                // in case when updated with no img and then updating with img
                if (preModel != null && preModel.getImage() != null) {
                    model.setImage(preModel.getImage());
                }

                String fileName = ioUtils.saveFile(model.getFile(), path);
                if (fileName != null) {
                    model.setImage(fileName);
                }
                postRepo.save(model);
                return new ResponseEntity<>(HttpStatus.OK);

            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("You can't do this action", HttpStatus.FORBIDDEN);
    }

    // TODO
    @PreAuthorize("authentication.name != 'anonymousUser'")
    public ResponseEntity<?> newLike(PostModel model) {

        Long id = model.getId();
        PostModel model2 = postRepo.findById(id.intValue());
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
    public ResponseEntity<?> deletePost(PostModel post) {
        UserModel userModel = userRepo.findUserById(postRepo.findById(post.getId().intValue()).getUser().getId());
        if (auth.getName().equals(userModel.getEmail()) || auth.getAuthorities().contains(Authority.OP_DELETE_POST)) {
            try {
                PostModel model = postRepo.findById(post.getId().intValue());

                Files.delete(Paths.get(ioUtils.getImagePath(model, path)));
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
        return postRepo.findById(id.intValue());
    }

}
