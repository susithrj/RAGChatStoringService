package com.codegensis.ragstore.dto.response;

import java.util.List;

public record MessagePageResponse(
    List<MessageResponse> messages,
    Integer page,
    Integer size,
    Long totalElements,
    Integer totalPages
) {
}
