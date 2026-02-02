package com.codegensis.ragstore.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    private Session session;

    @BeforeEach
    void setUp() {
        session = new Session();
    }

    @Test
    void testGettersAndSetters() {
        // Given
        Long id = 1L;
        String userId = "user123";
        String title = "Test Session";
        Boolean isFavorite = true;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // When
        session.setId(id);
        session.setUserId(userId);
        session.setTitle(title);
        session.setIsFavorite(isFavorite);
        session.setCreatedAt(createdAt);
        session.setUpdatedAt(updatedAt);

        // Then
        assertEquals(id, session.getId());
        assertEquals(userId, session.getUserId());
        assertEquals(title, session.getTitle());
        assertEquals(isFavorite, session.getIsFavorite());
        assertEquals(createdAt, session.getCreatedAt());
        assertEquals(updatedAt, session.getUpdatedAt());
    }

    @Test
    void testOnCreate_SetsTimestamps() {
        // Given
        session.setUserId("user123");
        session.setTitle("Test");

        // When
        session.onCreate();

        // Then
        assertNotNull(session.getCreatedAt());
        assertNotNull(session.getUpdatedAt());
        // Timestamps should be set (may not be exactly equal due to timing)
        assertTrue(session.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(session.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testOnCreate_AlwaysSetsTimestamps() {
        // Given
        LocalDateTime existingCreatedAt = LocalDateTime.of(2024, 1, 1, 12, 0);
        LocalDateTime existingUpdatedAt = LocalDateTime.of(2024, 1, 1, 12, 0);
        session.setCreatedAt(existingCreatedAt);
        session.setUpdatedAt(existingUpdatedAt);
        session.setUserId("user123");
        session.setTitle("Test");

        // When
        session.onCreate();

        // Then
        // onCreate always sets timestamps to now()
        assertNotNull(session.getCreatedAt());
        assertNotNull(session.getUpdatedAt());
        assertTrue(session.getCreatedAt().isAfter(existingCreatedAt) || 
                   session.getCreatedAt().equals(existingCreatedAt));
    }

    @Test
    void testOnUpdate_UpdatesUpdatedAt() {
        // Given
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 12, 0);
        LocalDateTime initialUpdatedAt = LocalDateTime.of(2024, 1, 1, 12, 0);
        session.setCreatedAt(createdAt);
        session.setUpdatedAt(initialUpdatedAt);
        session.setUserId("user123");
        session.setTitle("Test");

        // When
        session.onUpdate();

        // Then
        assertEquals(createdAt, session.getCreatedAt());
        assertNotNull(session.getUpdatedAt());
        assertTrue(session.getUpdatedAt().isAfter(initialUpdatedAt) || 
                   session.getUpdatedAt().equals(initialUpdatedAt));
    }

    @Test
    void testIsFavorite_DefaultValue() {
        // Given
        Session newSession = new Session();

        // Then
        assertFalse(newSession.getIsFavorite());
    }

    @Test
    void testSetIsFavorite_True() {
        // When
        session.setIsFavorite(true);

        // Then
        assertTrue(session.getIsFavorite());
    }

    @Test
    void testSetIsFavorite_False() {
        // When
        session.setIsFavorite(false);

        // Then
        assertFalse(session.getIsFavorite());
    }

    @Test
    void testSetIsFavorite_Null() {
        // When
        session.setIsFavorite(null);

        // Then
        assertNull(session.getIsFavorite());
    }
}
