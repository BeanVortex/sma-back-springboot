package ir.darkdeveloper.sma.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ir.darkdeveloper.sma.model.PostModel;
import ir.darkdeveloper.sma.service.PostService;

@RestController
@RequestMapping("/api/post")
public class PostController {

    private final PostService service;

    @Autowired
    public PostController(PostService service) {
        this.service = service;
    }

    @PostMapping("/")
    public ResponseEntity<?> savePost(HttpServletRequest request, @ModelAttribute PostModel model) {
        return service.savePost(request, model);
    }

    @GetMapping("/all/")
    public Page<PostModel> allPosts(Pageable pageable) {
        return service.allPosts(pageable);
    }

    @GetMapping("/{id}/")
    public PostModel getOnePost(@PathVariable("id") Long id) {
        return service.getOnePost(id);
    }

    @GetMapping("/search/")
    public Page<PostModel> searchPost(@RequestParam String content, @RequestParam(required = false) String title,
            Pageable pageable) {
        return service.searchPost(content, title, pageable);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deletePost(HttpServletRequest request, @RequestBody PostModel model) {
        return service.deletePost(request, model);
    }

    @GetMapping("/user/{id}/")
    public Page<PostModel> getOneUserPosts(@PathVariable("id") Long id, Pageable pageable) {
        return service.getOneUserPosts(id, pageable);
    }

}
