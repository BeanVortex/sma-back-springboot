package ir.darkdeveloper.sma.controllers;

import ir.darkdeveloper.sma.dto.CommentDto;
import ir.darkdeveloper.sma.dto.Mappers;
import ir.darkdeveloper.sma.model.CommentModel;
import ir.darkdeveloper.sma.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/api/post/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;
    private final Mappers mappers;

    @PostMapping("/{postId}/")
    @PreAuthorize("hasAuthority('OP_ADD_COMMENT')")
    public ResponseEntity<CommentDto> saveComment(@RequestBody CommentModel model,
                                                  @PathVariable Long postId,
                                                  HttpServletRequest req) {
        var savedComment = service.saveComment(Optional.ofNullable(model), postId, req);
        return ResponseEntity.ok(mappers.toDto(savedComment));
    }

    @PutMapping("/{commentId}/")
    @PreAuthorize("hasAuthority('OP_EDIT_COMMENT')")
    public ResponseEntity<CommentDto> updateComment(@RequestBody CommentModel model,
                                                    @PathVariable Long commentId,
                                                    HttpServletRequest req) {
        var updatedComment = service.updateComment(Optional.ofNullable(model), commentId, req);
        return ResponseEntity.ok(mappers.toDto(updatedComment));
    }


    @PutMapping("/like/{commentId}/")
    @PreAuthorize("hasAuthority('OP_ACCESS_USER')")
    public ResponseEntity<CommentDto> likeComment(@PathVariable Long commentId) {
        var savedComment = service.likeComment(commentId);
        return ResponseEntity.ok(mappers.toDto(savedComment));
    }


    @GetMapping("/{postId}/")
    public Page<CommentDto> getPostComments(@PathVariable("postId") Long postId, Pageable pageable) {
        return service.getPostComments(Optional.ofNullable(postId), pageable).map(mappers::toDto);
    }

    @DeleteMapping("/{commentId}/")
    @PreAuthorize("hasAuthority('OP_DELETE_COMMENT')")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId, HttpServletRequest req) {
        return ResponseEntity.ok(service.deleteComment(commentId, req));
    }
}
