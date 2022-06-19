package ir.darkdeveloper.sma.dto;

import java.time.LocalDateTime;

public record PostDto(Long id, String title,
                      String content, Long likes,
                      String image, Long userId,
                      LocalDateTime createdAt,
                      LocalDateTime updatedAt) {
}
