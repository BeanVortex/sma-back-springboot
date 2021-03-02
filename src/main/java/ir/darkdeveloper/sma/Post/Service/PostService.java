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
import ir.darkdeveloper.sma.Users.Models.UserModel;
import ir.darkdeveloper.sma.Users.Repo.UserRepo;
import ir.darkdeveloper.sma.Utils.IOUtils;
import ir.darkdeveloper.sma.Utils.UserUtils;

@Service
public class PostService {

    private final PostRepo postRepo;
    private final UserRepo userRepo;
    private final UserUtils userUtils;
    private final IOUtils ioUtils;
    private final String path = "posts/";
    private Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    @Autowired
    public PostService(PostRepo postRepo, UserRepo userRepo, UserUtils userUtils, IOUtils ioUtils) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.userUtils = userUtils;
        this.ioUtils = ioUtils;
    }

    @Transactional
    @PreAuthorize("authentication.name != 'anonymousUser'")
    public ResponseEntity<?> savePost(PostModel model) {
        try {
            // For updating Post img by deleting previous img and replacing with new one and
            // new name

            PostModel preModel = postRepo.findById(model.getId());
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

    @PreAuthorize("authentication.name != 'anonymousUser'")
    public ResponseEntity<?> newLike(PostModel model) {

        Long id = model.getId();
        PostModel model2 = postRepo.findById(id);
        Long likes = model2.getLikes();
        model2.setLikes(++likes);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public Page<PostModel> allPosts(Pageable pageable) {
        return postRepo.findAll(pageable);
    }

    public Page<PostModel> searchPost(String content, String title, Pageable pageable) {
        return postRepo.findByContentAndTitleContains(content, title, pageable);
    }

    @Transactional
    public ResponseEntity<?> deletePost(PostModel post) {
        try {
            UserModel userModel = userRepo.findUserById(postRepo.findById(post.getId()).getUser().getId());
            PostModel model = postRepo.findById(post.getId());

            // TODO delete admin access on production

            if (auth.getName().equals(userModel.getEmail()) || auth.getName().equals(userUtils.getAdminUsername())) {
                Files.delete(Paths.get(ioUtils.getImagePath(model, path)));
                postRepo.deleteById(post.getId());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("You can't do this action", HttpStatus.FORBIDDEN);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public PostModel getOnePost(Long id) {
        return postRepo.findById(id);
    }

}
