package ir.darkdeveloper.sma.controllers;

import jakarta.servlet.http.HttpServletRequest;

import ir.darkdeveloper.sma.dto.PostDto;
import ir.darkdeveloper.sma.dto.Mappers;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        return new ResponseEntity<>(mappers.toDto(savedPost), HttpStatus.CREATED);
    }

    @GetMapping("/all/")
    public ResponseEntity<Page<PostDto>> findAll(Pageable pageable) {
        var posts = service.findAll(pageable).map(mappers::toDto);
        return ResponseEntity.ok(posts);
    }


    @PutMapping("/{id}/")
    @PreAuthorize("hasAuthority('OP_EDIT_POST')")
    public ResponseEntity<PostDto> updatePost(@ModelAttribute PostModel model,
                                              @PathVariable Long id, HttpServletRequest req) {
        var savedPost = service.updatePost(Optional.ofNullable(model), id, req);
        return ResponseEntity.ok(mappers.toDto(savedPost));
    }

    @PutMapping("/like/{id}/")
    @PreAuthorize("hasAuthority('OP_ACCESS_USER')")
    public ResponseEntity<PostDto> likePost(@PathVariable Long id) {
        var savedPost = service.likePost(id);
        return ResponseEntity.ok(mappers.toDto(savedPost));
    }


    @GetMapping("/{id}/")
    public PostDto getOnePost(@PathVariable("id") Long id) {
        return mappers.toDto(service.getOnePost(id));
    }

    @GetMapping("/search/")
    public ResponseEntity<Page<PostDto>> searchPost(@RequestParam(required = false) String title,
                                                    @RequestParam String content,
                                                    Pageable pageable) {
        var posts = service.searchPost(content, title, pageable).map(mappers::toDto);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{id}/")
    public ResponseEntity<Page<PostDto>> getOneUserPosts(@PathVariable("id") Long id, Pageable pageable) {
        var posts = service.getOneUserPosts(id, pageable).map(mappers::toDto);
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping("/{id}/")
    @PreAuthorize("hasAuthority('OP_DELETE_POST')")
    public ResponseEntity<String> deletePost(@PathVariable Long id, HttpServletRequest req) {
        return ResponseEntity.ok(service.deletePost(id, req));
    }

}
