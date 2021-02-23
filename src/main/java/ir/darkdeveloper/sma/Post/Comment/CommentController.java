package ir.darkdeveloper.sma.Post.Comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post/comment")
@CrossOrigin("*")
public class CommentController {
    private final CommentService service;

    @Autowired
    public CommentController(CommentService service) {
        this.service = service;
    }

    @PostMapping("/")
    public ResponseEntity<?> saveComment(@RequestBody CommentModel model){
        return service.saveComment(model);
    }

    @DeleteMapping({"/{id}", "/{id}/"})
    public ResponseEntity<?> deleteComment(@PathVariable String id){
        return service.deleteComment(id);
    }
}
