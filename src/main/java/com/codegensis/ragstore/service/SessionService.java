package com.codegensis.ragstore.service;

import com.codegensis.ragstore.dto.request.CreateSessionRequest;
import com.codegensis.ragstore.dto.request.ToggleFavoriteRequest;
import com.codegensis.ragstore.dto.request.UpdateSessionRequest;
import com.codegensis.ragstore.dto.response.SessionResponse;
import com.codegensis.ragstore.entity.Session;
import com.codegensis.ragstore.exception.ResourceNotFoundException;
import com.codegensis.ragstore.mapper.SessionMapper;
import com.codegensis.ragstore.repository.SessionRepository;
import com.codegensis.ragstore.validator.SessionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing chat sessions.
 * Handles session creation, retrieval, updates, favorite toggling, and deletion.
 * All operations are transactional and include validation.
 */
@Service
@Transactional
public class SessionService {

    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);
    private static final String DEFAULT_TITLE = "New Chat";

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final SessionValidator sessionValidator;

    public SessionService(SessionRepository sessionRepository, 
                          SessionMapper sessionMapper,
                          SessionValidator sessionValidator) {
        this.sessionRepository = sessionRepository;
        this.sessionMapper = sessionMapper;
        this.sessionValidator = sessionValidator;
    }

    public SessionResponse createSession(CreateSessionRequest request) {
        sessionValidator.validateUserId(request.userId());
        
        Session session = new Session();
        session.setUserId(request.userId());
        session.setTitle(determineTitle(request.title()));
        session.setIsFavorite(false);

        Session saved = sessionRepository.save(session);
        logger.info("Session created - id: {}, userId: {}", saved.getId(), saved.getUserId());
        return sessionMapper.toResponse(saved);
    }

    public List<SessionResponse> getSessionsByUserId(String userId) {
        sessionValidator.validateUserId(userId);
        List<Session> sessions = sessionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return sessionMapper.toResponseList(sessions);
    }

    public SessionResponse getSessionById(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", sessionId));
        return sessionMapper.toResponse(session);
    }

    public SessionResponse updateSessionTitle(Long sessionId, UpdateSessionRequest request) {
        Session session = findSessionById(sessionId);
        sessionValidator.validateTitle(request.title());
        
        session.setTitle(request.title());
        Session updated = sessionRepository.save(session);
        logger.info("Session title updated - id: {}", sessionId);
        return sessionMapper.toResponse(updated);
    }

    public SessionResponse toggleFavorite(Long sessionId, ToggleFavoriteRequest request) {
        Session session = findSessionById(sessionId);
        session.setIsFavorite(request.isFavorite());
        Session updated = sessionRepository.save(session);
        logger.info("Session favorite toggled - id: {}, favorite: {}", sessionId, request.isFavorite());
        return sessionMapper.toResponse(updated);
    }

    public void deleteSession(Long sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("Session", sessionId);
        }
        sessionRepository.deleteById(sessionId);
        logger.info("Session deleted - id: {}", sessionId);
    }
    
    private Session findSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", sessionId));
    }
    
    private String determineTitle(String title) {
        return (title != null && !title.isBlank()) ? title : DEFAULT_TITLE;
    }
}
