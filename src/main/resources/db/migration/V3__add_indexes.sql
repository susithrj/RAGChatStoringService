-- Additional indexes for performance optimization
CREATE INDEX idx_sessions_is_favorite ON sessions(is_favorite);
CREATE INDEX idx_messages_session_timestamp ON messages(session_id, timestamp);
