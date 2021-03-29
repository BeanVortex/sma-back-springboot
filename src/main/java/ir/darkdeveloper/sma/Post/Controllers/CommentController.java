package ir.darkdeveloper.sma.Post.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ir.darkdeveloper.sma.Post.Models.CommentModel;
import ir.darkdeveloper.sma.Post.Service.CommentService;

@RestController
@RequestMapping("/api/post/comment")
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

    @DeleteMapping("/")
    public ResponseEntity<?> deleteComment(@RequestBody CommentModel comment){
        return service.deleteComment(comment);
    }

    @GetMapping("/{postId}/")
    public Page<CommentModel> getPostComments(@PathVariable("postId") Long postId, Pageable pageable){
        return service.getPostComments(postId, pageable);
    }
}
