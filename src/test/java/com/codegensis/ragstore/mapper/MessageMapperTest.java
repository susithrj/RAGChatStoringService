package com.codegensis.ragstore.mapper;

import com.codegensis.ragstore.dto.response.MessageResponse;
import com.codegensis.ragstore.entity.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageMapperTest {

    private MessageMapper messageMapper;

    @BeforeEach
    void setUp() {
        messageMapper = new MessageMapper();
    }

    @Test
    void toResponse_ValidMessage_ReturnsResponse() {
        // Given
        Message message = new Message();
        message.setId(1L);
        message.setSessionId(100L);
        message.setSender(Message.Sender.USER);
        message.setContent("Test content");
        message.setContext("Test context");
        message.setTimestamp(LocalDateTime.of(2024, 1, 1, 12, 0));

        // When
        MessageResponse response = messageMapper.toResponse(message);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(100L, response.sessionId());
        assertEquals("user", response.sender());
        assertEquals("Test content", response.content());
        assertEquals("Test context", response.context());
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), response.timestamp());
    }

    @Test
    void toResponse_MessageWithAssistantSender_ReturnsCorrectSender() {
        // Given
        Message message = new Message();
        message.setId(2L);
        message.setSessionId(100L);
        message.setSender(Message.Sender.ASSISTANT);
        message.setContent("Assistant response");
        message.setContext(null);
        message.setTimestamp(LocalDateTime.now());

        // When
        MessageResponse response = messageMapper.toResponse(message);

        // Then
        assertNotNull(response);
        assertEquals("assistant", response.sender());
        assertNull(response.context());
    }

    @Test
    void toResponse_MessageWithSystemSender_ReturnsCorrectSender() {
        // Given
        Message message = new Message();
        message.setId(3L);
        message.setSessionId(100L);
        message.setSender(Message.Sender.SYSTEM);
        message.setContent("System message");
        message.setContext(null);
        message.setTimestamp(LocalDateTime.now());

        // When
        MessageResponse response = messageMapper.toResponse(message);

        // Then
        assertNotNull(response);
        assertEquals("system", response.sender());
    }

    @Test
    void toResponse_NullMessage_ReturnsNull() {
        // When
        MessageResponse response = messageMapper.toResponse(null);

        // Then
        assertNull(response);
    }

    @Test
    void toResponse_MessageWithNullContext_ReturnsNullContext() {
        // Given
        Message message = new Message();
        message.setId(1L);
        message.setSessionId(100L);
        message.setSender(Message.Sender.USER);
        message.setContent("Content");
        message.setContext(null);
        message.setTimestamp(LocalDateTime.now());

        // When
        MessageResponse response = messageMapper.toResponse(message);

        // Then
        assertNotNull(response);
        assertNull(response.context());
    }

    @Test
    void toResponseList_ValidList_ReturnsResponseList() {
        // Given
        Message message1 = new Message();
        message1.setId(1L);
        message1.setSessionId(100L);
        message1.setSender(Message.Sender.USER);
        message1.setContent("Message 1");
        message1.setTimestamp(LocalDateTime.now());

        Message message2 = new Message();
        message2.setId(2L);
        message2.setSessionId(100L);
        message2.setSender(Message.Sender.ASSISTANT);
        message2.setContent("Message 2");
        message2.setTimestamp(LocalDateTime.now());

        List<Message> messages = Arrays.asList(message1, message2);

        // When
        List<MessageResponse> responses = messageMapper.toResponseList(messages);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).id());
        assertEquals(2L, responses.get(1).id());
        assertEquals("user", responses.get(0).sender());
        assertEquals("assistant", responses.get(1).sender());
    }

    @Test
    void toResponseList_EmptyList_ReturnsEmptyList() {
        // When
        List<MessageResponse> responses = messageMapper.toResponseList(Collections.emptyList());

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void toResponseList_ListWithNullMessage_HandlesNull() {
        // Given
        Message message = new Message();
        message.setId(1L);
        message.setSessionId(100L);
        message.setSender(Message.Sender.USER);
        message.setContent("Content");
        message.setTimestamp(LocalDateTime.now());

        List<Message> messages = Arrays.asList(message, null);

        // When
        List<MessageResponse> responses = messageMapper.toResponseList(messages);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertNotNull(responses.get(0));
        assertNull(responses.get(1));
    }
}
