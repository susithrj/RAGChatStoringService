package com.codegensis.ragstore.health;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DatabaseHealthIndicatorTest {

    private DatabaseHealthIndicator healthIndicator;
    private DataSource dataSource;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        healthIndicator = new DatabaseHealthIndicator(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
    }

    @Test
    void health_ValidConnection_ReturnsUp() throws SQLException {
        // Given
        when(connection.isValid(1)).thenReturn(true);

        // When
        Health health = healthIndicator.health();

        // Then
        assertNotNull(health);
        assertEquals(Status.UP, health.getStatus());
        assertEquals("H2", health.getDetails().get("database"));
        assertEquals("Connected", health.getDetails().get("status"));
        verify(connection).close();
    }

    @Test
    void health_InvalidConnection_ReturnsDown() throws SQLException {
        // Given
        when(connection.isValid(1)).thenReturn(false);

        // When
        Health health = healthIndicator.health();

        // Then
        assertNotNull(health);
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("H2", health.getDetails().get("database"));
        assertEquals("Connection invalid", health.getDetails().get("status"));
        verify(connection).close();
    }

    @Test
    void health_SQLException_ReturnsDown() throws SQLException {
        // Given
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection failed"));

        // When
        Health health = healthIndicator.health();

        // Then
        assertNotNull(health);
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("H2", health.getDetails().get("database"));
        assertNotNull(health.getDetails().get("error"));
    }
}
