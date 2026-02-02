package com.codegensis.ragstore.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    private Message message;

    @BeforeEach
    void setUp() {
        message = new Message();
    }

    @Test
    void testGettersAndSetters() {
        // Given
        Long id = 1L;
        Long sessionId = 100L;
        Message.Sender sender = Message.Sender.USER;
        String content = "Test content";
        String context = "Test context";
        LocalDateTime timestamp = LocalDateTime.now();
        Session session = new Session();

        // When
        message.setId(id);
        message.setSessionId(sessionId);
        message.setSender(sender);
        message.setContent(content);
        message.setContext(context);
        message.setTimestamp(timestamp);
        message.setSession(session);

        // Then
        assertEquals(id, message.getId());
        assertEquals(sessionId, message.getSessionId());
        assertEquals(sender, message.getSender());
        assertEquals(content, message.getContent());
        assertEquals(context, message.getContext());
        assertEquals(timestamp, message.getTimestamp());
        assertEquals(session, message.getSession());
    }

    @Test
    void testOnCreate_SetsTimestamp() {
        // Given
        message.setContent("Test");
        message.setSender(Message.Sender.USER);
        message.setSessionId(1L);

        // When
        message.onCreate();

        // Then
        assertNotNull(message.getTimestamp());
    }

    @Test
    void testOnCreate_PreservesExistingTimestamp() {
        // Given
        LocalDateTime existingTimestamp = LocalDateTime.of(2024, 1, 1, 12, 0);
        message.setTimestamp(existingTimestamp);
        message.setContent("Test");
        message.setSender(Message.Sender.USER);
        message.setSessionId(1L);

        // When
        message.onCreate();

        // Then
        assertEquals(existingTimestamp, message.getTimestamp());
    }

    @Test
    void testSenderEnum_GetDbValue() {
        // Then
        assertEquals("user", Message.Sender.USER.getDbValue());
        assertEquals("assistant", Message.Sender.ASSISTANT.getDbValue());
        assertEquals("system", Message.Sender.SYSTEM.getDbValue());
    }

    @Test
    void testSenderEnum_FromDbValue_ValidValues() {
        // Then
        assertEquals(Message.Sender.USER, Message.Sender.fromDbValue("user"));
        assertEquals(Message.Sender.USER, Message.Sender.fromDbValue("USER"));
        assertEquals(Message.Sender.USER, Message.Sender.fromDbValue("User"));
        assertEquals(Message.Sender.ASSISTANT, Message.Sender.fromDbValue("assistant"));
        assertEquals(Message.Sender.ASSISTANT, Message.Sender.fromDbValue("ASSISTANT"));
        assertEquals(Message.Sender.SYSTEM, Message.Sender.fromDbValue("system"));
        assertEquals(Message.Sender.SYSTEM, Message.Sender.fromDbValue("SYSTEM"));
    }

    @Test
    void testSenderEnum_FromDbValue_InvalidValues() {
        // Then
        assertNull(Message.Sender.fromDbValue("invalid"));
        assertNull(Message.Sender.fromDbValue(""));
        assertNull(Message.Sender.fromDbValue("   "));
        assertNull(Message.Sender.fromDbValue(null));
    }

    @Test
    void testSenderConverter_ConvertToDatabaseColumn() {
        // Given
        Message.Sender.Converter converter = new Message.Sender.Converter();

        // Then
        assertEquals("user", converter.convertToDatabaseColumn(Message.Sender.USER));
        assertEquals("assistant", converter.convertToDatabaseColumn(Message.Sender.ASSISTANT));
        assertEquals("system", converter.convertToDatabaseColumn(Message.Sender.SYSTEM));
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    void testSenderConverter_ConvertToEntityAttribute() {
        // Given
        Message.Sender.Converter converter = new Message.Sender.Converter();

        // Then
        assertEquals(Message.Sender.USER, converter.convertToEntityAttribute("user"));
        assertEquals(Message.Sender.ASSISTANT, converter.convertToEntityAttribute("assistant"));
        assertEquals(Message.Sender.SYSTEM, converter.convertToEntityAttribute("system"));
        assertNull(converter.convertToEntityAttribute(null));
        assertNull(converter.convertToEntityAttribute("invalid"));
    }

    @Test
    void testSetContext_Null() {
        // When
        message.setContext(null);

        // Then
        assertNull(message.getContext());
    }

    @Test
    void testSetSession_Null() {
        // When
        message.setSession(null);

        // Then
        assertNull(message.getSession());
    }
}
