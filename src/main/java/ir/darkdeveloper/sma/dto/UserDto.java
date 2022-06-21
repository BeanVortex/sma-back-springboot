package ir.darkdeveloper.sma.dto;

import java.time.LocalDateTime;

public record UserDto(Long id, String email, String userName,
                      Boolean enabled, String profilePicture,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {

}
