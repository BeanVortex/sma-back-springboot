package ir.darkdeveloper.sma.Post.Comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private final CommentRepo repo;

    @Autowired
    public CommentService(CommentRepo repo) {
        this.repo = repo;
    }

    public ResponseEntity<?> saveComment(CommentModel model) {
        try {
            repo.save(model);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> deleteComment(String id) {
        try {
            repo.deleteById(Long.valueOf(id));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    /*public Page<CommentModel> commentsOfPost(String id, Pageable pageable){
        return repo.findByPost(id, pageable);
    }*/
}
