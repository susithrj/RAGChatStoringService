package com.codegensis.ragstore.mapper;

import com.codegensis.ragstore.dto.response.SessionResponse;
import com.codegensis.ragstore.entity.Session;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SessionMapper {
    
    public SessionResponse toResponse(Session session) {
        if (session == null) {
            return null;
        }
        return new SessionResponse(
            session.getId(),
            session.getUserId(),
            session.getTitle(),
            session.getIsFavorite(),
            session.getCreatedAt(),
            session.getUpdatedAt()
        );
    }
    
    public List<SessionResponse> toResponseList(List<Session> sessions) {
        return sessions.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}
