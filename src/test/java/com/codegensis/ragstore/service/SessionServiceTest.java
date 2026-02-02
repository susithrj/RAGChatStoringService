package com.codegensis.ragstore.service;

import com.codegensis.ragstore.dto.request.CreateSessionRequest;
import com.codegensis.ragstore.dto.request.ToggleFavoriteRequest;
import com.codegensis.ragstore.dto.request.UpdateSessionRequest;
import com.codegensis.ragstore.dto.response.SessionResponse;
import com.codegensis.ragstore.entity.Session;
import com.codegensis.ragstore.exception.ResourceNotFoundException;
import com.codegensis.ragstore.exception.ValidationException;
import com.codegensis.ragstore.mapper.SessionMapper;
import com.codegensis.ragstore.repository.SessionRepository;
import com.codegensis.ragstore.validator.SessionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private SessionMapper sessionMapper;

    @Mock
    private SessionValidator sessionValidator;

    @InjectMocks
    private SessionService sessionService;

    private Long sessionId;
    private String userId;
    private Session session;
    private SessionResponse sessionResponse;

    @BeforeEach
    void setUp() {
        sessionId = 1L;
        userId = "user123";

        session = new Session();
        session.setId(sessionId);
        session.setUserId(userId);
        session.setTitle("Test Session");
        session.setIsFavorite(false);
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());

        sessionResponse = new SessionResponse(
            sessionId,
            userId,
            "Test Session",
            false,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Test
    void createSession_WithTitle_Success() {
        // Given
        CreateSessionRequest request = new CreateSessionRequest(userId, "My Session");
        Session savedSession = new Session();
        savedSession.setId(sessionId);
        savedSession.setUserId(userId);
        savedSession.setTitle("My Session");
        savedSession.setIsFavorite(false);

        when(sessionRepository.save(any(Session.class))).thenReturn(savedSession);
        when(sessionMapper.toResponse(savedSession)).thenReturn(sessionResponse);

        // When
        SessionResponse result = sessionService.createSession(request);

        // Then
        assertNotNull(result);
        verify(sessionValidator).validateUserId(userId);
        verify(sessionRepository).save(any(Session.class));
        verify(sessionMapper).toResponse(savedSession);
    }

    @Test
    void createSession_WithoutTitle_UsesDefault() {
        // Given
        CreateSessionRequest request = new CreateSessionRequest(userId, null);
        Session savedSession = new Session();
        savedSession.setId(sessionId);
        savedSession.setUserId(userId);
        savedSession.setTitle("New Chat");
        savedSession.setIsFavorite(false);

        when(sessionRepository.save(any(Session.class))).thenReturn(savedSession);
        when(sessionMapper.toResponse(savedSession)).thenReturn(sessionResponse);

        // When
        SessionResponse result = sessionService.createSession(request);

        // Then
        assertNotNull(result);
        verify(sessionRepository).save(argThat(s -> 
            "New Chat".equals(s.getTitle()) && 
            userId.equals(s.getUserId()) && 
            Boolean.FALSE.equals(s.getIsFavorite())
        ));
    }

    @Test
    void createSession_InvalidUserId_ThrowsException() {
        // Given
        CreateSessionRequest request = new CreateSessionRequest("invalid user!", "Title");
        doThrow(new ValidationException("User ID must contain only alphanumeric characters and underscores"))
            .when(sessionValidator).validateUserId("invalid user!");

        // When & Then
        assertThrows(ValidationException.class, () -> {
            sessionService.createSession(request);
        });

        verify(sessionValidator).validateUserId("invalid user!");
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void getSessionsByUserId_Success() {
        // Given
        List<Session> sessions = List.of(session);
        List<SessionResponse> responses = List.of(sessionResponse);

        when(sessionRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(sessions);
        when(sessionMapper.toResponseList(sessions)).thenReturn(responses);

        // When
        List<SessionResponse> result = sessionService.getSessionsByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(sessionValidator).validateUserId(userId);
        verify(sessionRepository).findByUserIdOrderByCreatedAtDesc(userId);
        verify(sessionMapper).toResponseList(sessions);
    }

    @Test
    void getSessionsByUserId_EmptyList() {
        // Given
        when(sessionRepository.findByUserIdOrderByCreatedAtDesc(userId))
            .thenReturn(Collections.emptyList());
        when(sessionMapper.toResponseList(Collections.emptyList()))
            .thenReturn(Collections.emptyList());

        // When
        List<SessionResponse> result = sessionService.getSessionsByUserId(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getSessionById_Success() {
        // Given
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(sessionMapper.toResponse(session)).thenReturn(sessionResponse);

        // When
        SessionResponse result = sessionService.getSessionById(sessionId);

        // Then
        assertNotNull(result);
        assertEquals(sessionResponse, result);
        verify(sessionRepository).findById(sessionId);
        verify(sessionMapper).toResponse(session);
    }

    @Test
    void getSessionById_NotFound_ThrowsException() {
        // Given
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            sessionService.getSessionById(sessionId);
        });

        verify(sessionRepository).findById(sessionId);
        verify(sessionMapper, never()).toResponse(any());
    }

    @Test
    void updateSessionTitle_Success() {
        // Given
        UpdateSessionRequest request = new UpdateSessionRequest("Updated Title");
        Session updatedSession = new Session();
        updatedSession.setId(sessionId);
        updatedSession.setTitle("Updated Title");
        SessionResponse updatedResponse = new SessionResponse(
            sessionId, userId, "Updated Title", false, LocalDateTime.now(), LocalDateTime.now()
        );

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(updatedSession);
        when(sessionMapper.toResponse(updatedSession)).thenReturn(updatedResponse);

        // When
        SessionResponse result = sessionService.updateSessionTitle(sessionId, request);

        // Then
        assertNotNull(result);
        verify(sessionRepository).findById(sessionId);
        verify(sessionValidator).validateTitle("Updated Title");
        verify(sessionRepository).save(argThat(s -> "Updated Title".equals(s.getTitle())));
        verify(sessionMapper).toResponse(updatedSession);
    }

    @Test
    void updateSessionTitle_SessionNotFound_ThrowsException() {
        // Given
        UpdateSessionRequest request = new UpdateSessionRequest("Updated Title");
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            sessionService.updateSessionTitle(sessionId, request);
        });

        verify(sessionRepository).findById(sessionId);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void updateSessionTitle_InvalidTitle_ThrowsException() {
        // Given
        UpdateSessionRequest request = new UpdateSessionRequest("");
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        doThrow(new ValidationException("Title cannot be empty"))
            .when(sessionValidator).validateTitle("");

        // When & Then
        assertThrows(ValidationException.class, () -> {
            sessionService.updateSessionTitle(sessionId, request);
        });

        verify(sessionValidator).validateTitle("");
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void toggleFavorite_SetToTrue_Success() {
        // Given
        ToggleFavoriteRequest request = new ToggleFavoriteRequest(true);
        Session updatedSession = new Session();
        updatedSession.setId(sessionId);
        updatedSession.setIsFavorite(true);
        SessionResponse updatedResponse = new SessionResponse(
            sessionId, userId, "Test Session", true, LocalDateTime.now(), LocalDateTime.now()
        );

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(updatedSession);
        when(sessionMapper.toResponse(updatedSession)).thenReturn(updatedResponse);

        // When
        SessionResponse result = sessionService.toggleFavorite(sessionId, request);

        // Then
        assertNotNull(result);
        verify(sessionRepository).findById(sessionId);
        verify(sessionRepository).save(argThat(s -> Boolean.TRUE.equals(s.getIsFavorite())));
        verify(sessionMapper).toResponse(updatedSession);
    }

    @Test
    void toggleFavorite_SetToFalse_Success() {
        // Given
        ToggleFavoriteRequest request = new ToggleFavoriteRequest(false);
        session.setIsFavorite(true); // Start as favorite
        Session updatedSession = new Session();
        updatedSession.setId(sessionId);
        updatedSession.setIsFavorite(false);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(updatedSession);
        when(sessionMapper.toResponse(updatedSession)).thenReturn(sessionResponse);

        // When
        SessionResponse result = sessionService.toggleFavorite(sessionId, request);

        // Then
        assertNotNull(result);
        verify(sessionRepository).save(argThat(s -> Boolean.FALSE.equals(s.getIsFavorite())));
    }

    @Test
    void toggleFavorite_SessionNotFound_ThrowsException() {
        // Given
        ToggleFavoriteRequest request = new ToggleFavoriteRequest(true);
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            sessionService.toggleFavorite(sessionId, request);
        });

        verify(sessionRepository).findById(sessionId);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void deleteSession_Success() {
        // Given
        when(sessionRepository.existsById(sessionId)).thenReturn(true);
        doNothing().when(sessionRepository).deleteById(sessionId);

        // When
        sessionService.deleteSession(sessionId);

        // Then
        verify(sessionRepository).existsById(sessionId);
        verify(sessionRepository).deleteById(sessionId);
    }

    @Test
    void deleteSession_NotFound_ThrowsException() {
        // Given
        when(sessionRepository.existsById(sessionId)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            sessionService.deleteSession(sessionId);
        });

        verify(sessionRepository).existsById(sessionId);
        verify(sessionRepository, never()).deleteById(any());
    }
}
