package ir.darkdeveloper.sma.controllers;

import javax.servlet.http.HttpServletRequest;

import ir.darkdeveloper.sma.dto.PostDto;
import ir.darkdeveloper.sma.dto.Mappers;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import java.util.Optional;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService service;
    private final Mappers mappers;

    @PostMapping("/")
    @PreAuthorize("hasAuthority('OP_ADD_POST')")
    public ResponseEntity<PostDto> savePost(@ModelAttribute PostModel model, HttpServletRequest req) {
        var savedPost = service.savePost(Optional.ofNullable(model), req);
        return ResponseEntity.ok(mappers.toDto(savedPost));
    }

    @GetMapping("/all/")
    public ResponseEntity<Page<PostDto>> findAll(Pageable pageable) {
        var posts = service.findAll(pageable).map(mappers::toDto);
        return ResponseEntity.ok(posts);
    }

    // TODO update

    @GetMapping("/{id}/")
    public PostModel getOnePost(@PathVariable("id") Long id) {
        return service.getOnePost(id);
    }

    @GetMapping("/search/")
    public ResponseEntity<Page<PostDto>> searchPost(@RequestParam(required = false) String title,
                                                    @RequestParam String content,
                                                    Pageable pageable) {
        var posts = service.searchPost(content, title, pageable).map(mappers::toDto);
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('OP_DELETE_POST')")
    public ResponseEntity<String> deletePost(@PathVariable Long id, HttpServletRequest req) {
        return ResponseEntity.ok(service.deletePost(id, req));
    }

    @GetMapping("/user/{id}/")
    public ResponseEntity<Page<PostDto>> getOneUserPosts(@PathVariable("id") Long id, Pageable pageable) {
        var posts = service.getOneUserPosts(id, pageable).map(mappers::toDto);
        return ResponseEntity.ok(posts);
    }

}
