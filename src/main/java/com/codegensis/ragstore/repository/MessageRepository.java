package com.codegensis.ragstore.repository;

import com.codegensis.ragstore.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    Page<Message> findBySessionIdOrderByTimestampAsc(Long sessionId, Pageable pageable);
}
