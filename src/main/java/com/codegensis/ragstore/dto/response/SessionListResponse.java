package com.codegensis.ragstore.dto.response;

import java.util.List;

public record SessionListResponse(
    List<SessionResponse> sessions,
    Long total
) {
}
