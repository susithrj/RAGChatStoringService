package com.codegensis.ragstore.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record MessageResponse(
    Long id,
    Long sessionId,
    String sender,
    String content,
    String context,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    LocalDateTime timestamp
) {
}
