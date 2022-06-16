package ir.darkdeveloper.sma.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ir.darkdeveloper.sma.model.CommentModel;
import ir.darkdeveloper.sma.service.CommentService;

import javax.swing.text.html.Option;
import java.util.Optional;

@RestController
@RequestMapping("/api/post/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;

    @PostMapping("/")
    @PreAuthorize("authentication.name != 'anonymousUser'")
    public ResponseEntity<CommentModel> saveComment(@RequestBody CommentModel model) {
        return service.saveComment(Optional.ofNullable(model));
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteComment(@RequestBody CommentModel comment) {
        return service.deleteComment(Optional.ofNullable(comment));
    }

    @GetMapping("/{postId}/")
    public Page<CommentModel> getPostComments(@PathVariable("postId") Long postId, Pageable pageable) {
        return service.getPostComments(Optional.ofNullable(postId), pageable);
    }
}
