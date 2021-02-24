package ir.darkdeveloper.sma.Post.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ir.darkdeveloper.sma.Post.Models.PostModel;
import ir.darkdeveloper.sma.Post.Service.PostService;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class PostController {

    private final PostService service;

    @Autowired
    public PostController(PostService service) {
        this.service = service;
    }

    @PostMapping({"/post", "/post/"})
    public ResponseEntity<?> savePost(@ModelAttribute PostModel model) {
        return service.savePost(model);
    }

    @GetMapping({"/post", "/post/"})
    public Page<PostModel> allPosts(Pageable pageable){
        return service.allPosts(pageable);
    }

    @GetMapping({"/post/{id}", "/post/{id}/"})
    public PostModel getOnePost(@PathVariable("id") Long id){
        return service.getOnePost(id);
    }

    @GetMapping({"/post/search", "/post/search/"})
    public Page<PostModel> searchPost(@RequestParam String content, @RequestParam String title, Pageable pageable) {
        return service.searchPost(content, title, pageable);
    }

    @DeleteMapping({"/post/", "/post"})
    public ResponseEntity<?> deletePost(@RequestBody PostModel model){
        return service.deletePost(model);
    }

}
