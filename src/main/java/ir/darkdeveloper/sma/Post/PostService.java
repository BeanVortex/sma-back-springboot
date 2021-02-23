package ir.darkdeveloper.sma.Post;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PostService {

    private final PostRepo repo;

    @Autowired
    public PostService(PostRepo repo) {
        this.repo = repo;
    }

    @Transactional
    public ResponseEntity<?> savePost(PostModel model) {
        try {
            //For updating Post img by deleting previous img and replacing with new one and new name
            PostModel preModel = repo.findById(model.getId());
            if (model.getId() != null && model.getFile() != null) {
                String path = ResourceUtils.getFile("classpath:static/img/").getAbsolutePath()
                        + File.separator + preModel.getImage();
                Files.delete(Paths.get(path));
            }
            // in case when updated with no img and then updating with img
            if (preModel != null && preModel.getImage() != null){
                model.setImage(preModel.getImage());
            }

            String fileName = saveFile(model.getFile());
            if (fileName != null) {
                model.setImage(fileName);
            }
            repo.save(model);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> newLike(PostModel model){

        long id = model.getId();
        PostModel model2 = repo.findById(id);
        int likes = model2.getLikes();
        likes++;
        model2.setLikes(likes);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String saveFile(MultipartFile file) throws Exception {
        if (file != null) {
            // first it may not upload and save file in the path. should create static/img folder in resources
            String path = ResourceUtils.getFile("classpath:static/img/").getAbsolutePath();
            byte[] bytes = file.getBytes();
            String fileName = UUID.randomUUID() + "." + Objects.requireNonNull(file.getContentType()).split("/")[1];
            Files.write(Paths.get(path + File.separator + fileName), bytes);
            return fileName;
        }
        return null;
    }

    public Page<PostModel> allPosts(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Page<PostModel> searchPost(String content, String title, Pageable pageable) {
        return repo.findByContentAndTitleContains(content, title, pageable);
    }

    @Transactional
    public ResponseEntity<?> deletePost(Long id) {
        try {
            PostModel model = repo.findById(id);
            String path = ResourceUtils.getFile("classpath:static/img/").getAbsolutePath()
                    + File.separator + model.getImage();
            Files.delete(Paths.get(path));
            repo.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

	public PostModel getOnePost(Long id) {
        return repo.findById(id);
	}
}
