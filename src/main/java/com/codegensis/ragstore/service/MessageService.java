package com.codegensis.ragstore.service;

import com.codegensis.ragstore.dto.request.AddMessageRequest;
import com.codegensis.ragstore.dto.response.MessagePageResponse;
import com.codegensis.ragstore.dto.response.MessageResponse;
import com.codegensis.ragstore.entity.Message;
import com.codegensis.ragstore.exception.ResourceNotFoundException;
import com.codegensis.ragstore.mapper.MessageMapper;
import com.codegensis.ragstore.repository.MessageRepository;
import com.codegensis.ragstore.repository.SessionRepository;
import com.codegensis.ragstore.validator.MessageValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing chat messages within sessions.
 * Handles message creation, retrieval with pagination, and validation.
 * All operations are transactional and require an existing session.
 */
@Service
@Transactional
public class MessageService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    
    private final MessageRepository messageRepository;
    private final SessionRepository sessionRepository;
    private final MessageMapper messageMapper;
    private final MessageValidator messageValidator;
    
    public MessageService(MessageRepository messageRepository,
                         SessionRepository sessionRepository,
                         MessageMapper messageMapper,
                         MessageValidator messageValidator) {
        this.messageRepository = messageRepository;
        this.sessionRepository = sessionRepository;
        this.messageMapper = messageMapper;
        this.messageValidator = messageValidator;
    }
    
    public MessageResponse addMessage(Long sessionId, AddMessageRequest request) {
        verifySessionExists(sessionId);
        
        Message.Sender sender = messageValidator.validateAndParseSender(request.sender());
        messageValidator.validateContent(request.content());
        messageValidator.validateContext(request.context());
        
        Message message = createMessage(sessionId, sender, request);
        Message saved = messageRepository.save(message);
        logger.info("Message created - id: {}, sessionId: {}", saved.getId(), sessionId);
        
        return messageMapper.toResponse(saved);
    }
    
    public MessagePageResponse getMessages(Long sessionId, int page, int size) {
        verifySessionExists(sessionId);
        messageValidator.validatePagination(page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messagePage = messageRepository.findBySessionIdOrderByTimestampAsc(sessionId, pageable);
        
        List<MessageResponse> messages = messageMapper.toResponseList(messagePage.getContent());
        
        return new MessagePageResponse(
            messages,
            page,
            size,
            messagePage.getTotalElements(),
            messagePage.getTotalPages()
        );
    }
    
    private void verifySessionExists(Long sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("Session", sessionId);
        }
    }
    
    private Message createMessage(Long sessionId, Message.Sender sender, AddMessageRequest request) {
        Message message = new Message();
        message.setSessionId(sessionId);
        message.setSender(sender);
        message.setContent(request.content());
        message.setContext(request.context());
        return message;
    }
}
