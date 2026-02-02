package com.codegensis.ragstore.mapper;

import com.codegensis.ragstore.dto.response.SessionResponse;
import com.codegensis.ragstore.entity.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SessionMapperTest {

    private SessionMapper sessionMapper;

    @BeforeEach
    void setUp() {
        sessionMapper = new SessionMapper();
    }

    @Test
    void toResponse_ValidSession_ReturnsResponse() {
        // Given
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 12, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 2, 12, 0);
        
        Session session = new Session();
        session.setId(1L);
        session.setUserId("user123");
        session.setTitle("Test Session");
        session.setIsFavorite(true);
        session.setCreatedAt(createdAt);
        session.setUpdatedAt(updatedAt);

        // When
        SessionResponse response = sessionMapper.toResponse(session);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("user123", response.userId());
        assertEquals("Test Session", response.title());
        assertTrue(response.isFavorite());
        assertEquals(createdAt, response.createdAt());
        assertEquals(updatedAt, response.updatedAt());
    }

    @Test
    void toResponse_SessionNotFavorite_ReturnsFalse() {
        // Given
        Session session = new Session();
        session.setId(1L);
        session.setUserId("user123");
        session.setTitle("Test Session");
        session.setIsFavorite(false);
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());

        // When
        SessionResponse response = sessionMapper.toResponse(session);

        // Then
        assertNotNull(response);
        assertFalse(response.isFavorite());
    }

    @Test
    void toResponse_NullSession_ReturnsNull() {
        // When
        SessionResponse response = sessionMapper.toResponse(null);

        // Then
        assertNull(response);
    }

    @Test
    void toResponse_SessionWithLongTitle_ReturnsResponse() {
        // Given
        String longTitle = "A".repeat(100);
        Session session = new Session();
        session.setId(1L);
        session.setUserId("user123");
        session.setTitle(longTitle);
        session.setIsFavorite(false);
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());

        // When
        SessionResponse response = sessionMapper.toResponse(session);

        // Then
        assertNotNull(response);
        assertEquals(longTitle, response.title());
    }

    @Test
    void toResponseList_ValidList_ReturnsResponseList() {
        // Given
        Session session1 = new Session();
        session1.setId(1L);
        session1.setUserId("user123");
        session1.setTitle("Session 1");
        session1.setIsFavorite(true);
        session1.setCreatedAt(LocalDateTime.now());
        session1.setUpdatedAt(LocalDateTime.now());

        Session session2 = new Session();
        session2.setId(2L);
        session2.setUserId("user123");
        session2.setTitle("Session 2");
        session2.setIsFavorite(false);
        session2.setCreatedAt(LocalDateTime.now());
        session2.setUpdatedAt(LocalDateTime.now());

        List<Session> sessions = Arrays.asList(session1, session2);

        // When
        List<SessionResponse> responses = sessionMapper.toResponseList(sessions);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).id());
        assertEquals(2L, responses.get(1).id());
        assertTrue(responses.get(0).isFavorite());
        assertFalse(responses.get(1).isFavorite());
    }

    @Test
    void toResponseList_EmptyList_ReturnsEmptyList() {
        // When
        List<SessionResponse> responses = sessionMapper.toResponseList(Collections.emptyList());

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void toResponseList_ListWithNullSession_HandlesNull() {
        // Given
        Session session = new Session();
        session.setId(1L);
        session.setUserId("user123");
        session.setTitle("Session");
        session.setIsFavorite(false);
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());

        List<Session> sessions = Arrays.asList(session, null);

        // When
        List<SessionResponse> responses = sessionMapper.toResponseList(sessions);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertNotNull(responses.get(0));
        assertNull(responses.get(1));
    }

    @Test
    void toResponse_SessionWithSameTimestamps_ReturnsResponse() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        Session session = new Session();
        session.setId(1L);
        session.setUserId("user123");
        session.setTitle("Session");
        session.setIsFavorite(false);
        session.setCreatedAt(timestamp);
        session.setUpdatedAt(timestamp);

        // When
        SessionResponse response = sessionMapper.toResponse(session);

        // Then
        assertNotNull(response);
        assertEquals(timestamp, response.createdAt());
        assertEquals(timestamp, response.updatedAt());
    }
}
