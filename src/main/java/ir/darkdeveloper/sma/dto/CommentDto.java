package ir.darkdeveloper.sma.dto;

import java.time.LocalDateTime;

public record CommentDto(Long id, String content,
                         Long likes, Long postId,
                         Long userId,
                         LocalDateTime createdAt,
                         LocalDateTime updatedAt) {
}
