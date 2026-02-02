package com.codegensis.ragstore.dto.request;

import jakarta.validation.constraints.NotNull;

public record ToggleFavoriteRequest(
    @NotNull(message = "isFavorite is required")
    Boolean isFavorite
) {
}
