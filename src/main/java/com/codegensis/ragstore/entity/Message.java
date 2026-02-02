package com.codegensis.ragstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_messages_session_id", columnList = "sessionId"),
    @Index(name = "idx_messages_timestamp", columnList = "timestamp"),
    @Index(name = "idx_messages_session_timestamp", columnList = "sessionId, timestamp")
})
public class Message {
    
    public enum Sender {
        USER("user"),
        ASSISTANT("assistant"),
        SYSTEM("system");
        
        private final String dbValue;
        
        Sender(String dbValue) {
            this.dbValue = dbValue;
        }
        
        public String getDbValue() {
            return dbValue;
        }
        
        public static Sender fromDbValue(String dbValue) {
            if (dbValue == null || dbValue.isBlank()) {
                return null;
            }
            for (Sender sender : values()) {
                if (sender.dbValue.equalsIgnoreCase(dbValue)) {
                    return sender;
                }
            }
            return null;
        }
        
        @jakarta.persistence.Converter
        public static class Converter implements jakarta.persistence.AttributeConverter<Sender, String> {
            
            @Override
            public String convertToDatabaseColumn(Sender sender) {
                if (sender == null) {
                    return null;
                }
                return sender.getDbValue();
            }
            
            @Override
            public Sender convertToEntityAttribute(String dbData) {
                return Sender.fromDbValue(dbData);
            }
        }
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "session_id", nullable = false)
    @NotNull(message = "Session ID is required")
    private Long sessionId;
    
    @Column(nullable = false, length = 20)
    @Convert(converter = Sender.Converter.class)
    @NotNull(message = "Sender is required")
    private Sender sender;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Content is required")
    @Size(max = 10000, message = "Content must not exceed 10,000 characters")
    private String content;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 51200, message = "Context must not exceed 50KB")
    private String context;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private Session session;
    
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
    
    public Sender getSender() {
        return sender;
    }
    
    public void setSender(Sender sender) {
        this.sender = sender;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getContext() {
        return context;
    }
    
    public void setContext(String context) {
        this.context = context;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Session getSession() {
        return session;
    }
    
    public void setSession(Session session) {
        this.session = session;
    }
}
