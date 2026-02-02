package com.codegensis.ragstore.service;

import com.codegensis.ragstore.dto.request.AddMessageRequest;
import com.codegensis.ragstore.dto.response.MessagePageResponse;
import com.codegensis.ragstore.dto.response.MessageResponse;
import com.codegensis.ragstore.entity.Message;
import com.codegensis.ragstore.exception.ResourceNotFoundException;
import com.codegensis.ragstore.exception.ValidationException;
import com.codegensis.ragstore.mapper.MessageMapper;
import com.codegensis.ragstore.repository.MessageRepository;
import com.codegensis.ragstore.repository.SessionRepository;
import com.codegensis.ragstore.validator.MessageValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private MessageValidator messageValidator;

    @InjectMocks
    private MessageService messageService;

    private Long sessionId;
    private AddMessageRequest addMessageRequest;
    private Message message;
    private MessageResponse messageResponse;

    @BeforeEach
    void setUp() {
        sessionId = 1L;
        
        addMessageRequest = new AddMessageRequest(
            "user",
            "Test message content",
            "Test context"
        );

        message = new Message();
        message.setId(1L);
        message.setSessionId(sessionId);
        message.setSender(Message.Sender.USER);
        message.setContent("Test message content");
        message.setContext("Test context");
        message.setTimestamp(LocalDateTime.now());

        messageResponse = new MessageResponse(
            1L,
            sessionId,
            "user",
            "Test message content",
            "Test context",
            LocalDateTime.now()
        );
    }

    @Test
    void addMessage_Success() {
        // Given
        when(sessionRepository.existsById(sessionId)).thenReturn(true);
        when(messageValidator.validateAndParseSender("user")).thenReturn(Message.Sender.USER);
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(messageMapper.toResponse(message)).thenReturn(messageResponse);

        // When
        MessageResponse result = messageService.addMessage(sessionId, addMessageRequest);

        // Then
        assertNotNull(result);
        assertEquals(messageResponse, result);
        verify(sessionRepository).existsById(sessionId);
        verify(messageValidator).validateAndParseSender("user");
        verify(messageValidator).validateContent("Test message content");
        verify(messageValidator).validateContext("Test context");
        verify(messageRepository).save(any(Message.class));
        verify(messageMapper).toResponse(message);
    }

    @Test
    void addMessage_SessionNotFound_ThrowsException() {
        // Given
        when(sessionRepository.existsById(sessionId)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            messageService.addMessage(sessionId, addMessageRequest);
        });

        verify(sessionRepository).existsById(sessionId);
        verify(messageRepository, never()).save(any());
    }

    @Test
    void addMessage_InvalidSender_ThrowsException() {
        // Given
        when(sessionRepository.existsById(sessionId)).thenReturn(true);
        when(messageValidator.validateAndParseSender("invalid")).thenThrow(
            new ValidationException("Sender must be one of: user, assistant, system")
        );

        AddMessageRequest invalidRequest = new AddMessageRequest("invalid", "content", null);

        // When & Then
        assertThrows(ValidationException.class, () -> {
            messageService.addMessage(sessionId, invalidRequest);
        });

        verify(sessionRepository).existsById(sessionId);
        verify(messageValidator).validateAndParseSender("invalid");
        verify(messageRepository, never()).save(any());
    }

    @Test
    void addMessage_InvalidContent_ThrowsException() {
        // Given
        when(sessionRepository.existsById(sessionId)).thenReturn(true);
        when(messageValidator.validateAndParseSender("user")).thenReturn(Message.Sender.USER);
        doThrow(new ValidationException("Content is required"))
            .when(messageValidator).validateContent("");

        AddMessageRequest invalidRequest = new AddMessageRequest("user", "", null);

        // When & Then
        assertThrows(ValidationException.class, () -> {
            messageService.addMessage(sessionId, invalidRequest);
        });

        verify(messageValidator).validateContent("");
        verify(messageRepository, never()).save(any());
    }

    @Test
    void getMessages_Success() {
        // Given
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);
        List<Message> messages = List.of(message);
        Page<Message> messagePage = new PageImpl<>(messages, pageable, 1);

        when(sessionRepository.existsById(sessionId)).thenReturn(true);
        when(messageRepository.findBySessionIdOrderByTimestampAsc(sessionId, pageable))
            .thenReturn(messagePage);
        when(messageMapper.toResponseList(messages)).thenReturn(List.of(messageResponse));

        // When
        MessagePageResponse result = messageService.getMessages(sessionId, page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.messages().size());
        assertEquals(page, result.page());
        assertEquals(size, result.size());
        assertEquals(1L, result.totalElements());
        assertEquals(1, result.totalPages());

        verify(sessionRepository).existsById(sessionId);
        verify(messageValidator).validatePagination(page, size);
        verify(messageRepository).findBySessionIdOrderByTimestampAsc(sessionId, pageable);
        verify(messageMapper).toResponseList(messages);
    }

    @Test
    void getMessages_EmptyPage() {
        // Given
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(sessionRepository.existsById(sessionId)).thenReturn(true);
        when(messageRepository.findBySessionIdOrderByTimestampAsc(sessionId, pageable))
            .thenReturn(emptyPage);
        when(messageMapper.toResponseList(Collections.emptyList()))
            .thenReturn(Collections.emptyList());

        // When
        MessagePageResponse result = messageService.getMessages(sessionId, page, size);

        // Then
        assertNotNull(result);
        assertTrue(result.messages().isEmpty());
        assertEquals(0L, result.totalElements());
        assertEquals(0, result.totalPages());
    }

    @Test
    void getMessages_SessionNotFound_ThrowsException() {
        // Given
        when(sessionRepository.existsById(sessionId)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            messageService.getMessages(sessionId, 0, 20);
        });

        verify(sessionRepository).existsById(sessionId);
        verify(messageRepository, never()).findBySessionIdOrderByTimestampAsc(any(), any());
    }

    @Test
    void getMessages_InvalidPagination_ThrowsException() {
        // Given
        when(sessionRepository.existsById(sessionId)).thenReturn(true);
        doThrow(new ValidationException("Page must be >= 0"))
            .when(messageValidator).validatePagination(-1, 20);

        // When & Then
        assertThrows(ValidationException.class, () -> {
            messageService.getMessages(sessionId, -1, 20);
        });

        verify(messageValidator).validatePagination(-1, 20);
        verify(messageRepository, never()).findBySessionIdOrderByTimestampAsc(any(), any());
    }

    @Test
    void getMessages_InvalidPageSize_ThrowsException() {
        // Given
        when(sessionRepository.existsById(sessionId)).thenReturn(true);
        doThrow(new ValidationException("Size must be between 1 and 100"))
            .when(messageValidator).validatePagination(0, 0);

        // When & Then
        assertThrows(ValidationException.class, () -> {
            messageService.getMessages(sessionId, 0, 0);
        });

        verify(messageValidator).validatePagination(0, 0);
        verify(messageRepository, never()).findBySessionIdOrderByTimestampAsc(any(), any());
    }
}
